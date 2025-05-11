package gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.RedisHeaders;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.entity.dto.AuthUserDTO;
import common.core.utils.JsonUtils;
import common.core.entity.dto.TokenDTO;
import gateway.clients.WebClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import common.security.entity.SecurityHeaders;
import common.security.utils.JwtUtil;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author abing
 * @created 2025/4/16 16:42
 * 全局过滤器
 */

@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate; // 改为响应式模板

    @Resource
    private WebClientService webClientService;

    @Resource
    private ObjectMapper objectMapper;

    private static final List<String> whiteList = List.of("/auth/login", "/user/register");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 预检请求直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod().toString())) {
            return chain.filter(exchange);
        }

        // 白名单检查
        if (whiteList.stream().anyMatch(request.getURI().getPath()::startsWith)) {
            return chain.filter(exchange);
        }

        String uuid = extractToken(request);
        if (!StringUtils.hasText(uuid)) {
            return unauthorizedResponse(exchange.getResponse(), "缺少访问令牌", StatusCode.UNKNOWN_EXCEPTION);
        }

        String cacheKey = RedisHeaders.GATEWAY_CACHE + uuid;

        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(parsedUserInfo -> {
                    try {
                        Map<String, String> userInfoMap = objectMapper.readValue(
                                parsedUserInfo, new TypeReference<Map<String, String>>() {
                                }
                        );
                        return addHeadersAndContinue(exchange, chain, userInfoMap);
                    } catch (JsonProcessingException e) {
                        log.error("解析缓存用户信息失败", e);
                        return redisTemplate.delete(cacheKey)
                                .then(unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> checkTokenAndRefresh(uuid, exchange, chain, cacheKey)))
                .onErrorResume(e -> {
                    log.error("认证流程异常", e);
                    return unauthorizedResponse(exchange.getResponse(), "系统异常", StatusCode.UNKNOWN_EXCEPTION);
                });
    }

    private Mono<Void> checkTokenAndRefresh(String uuid, ServerWebExchange exchange, GatewayFilterChain chain, String cacheKey) {
        String accessTokenKey = RedisHeaders.ACCESS_TOKEN + uuid;

        return redisTemplate.opsForValue().get(accessTokenKey)
                .flatMap(token -> {
                    if (!JwtUtil.verifyToken(token)) {
                        return redisTemplate.delete(accessTokenKey)
                                .then(unauthorizedResponse(exchange.getResponse(), "令牌已过期或不可用", StatusCode.TOKEN_EXPIRE_EXCEPTION));
                    }

                    Map<String, String> userInfoMap = JwtUtil.parseToken(token);
                    if (userInfoMap.isEmpty()) {
                        return unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION);
                    }

                    try {
                        String json = objectMapper.writeValueAsString(userInfoMap);
                        long expireTime = JwtUtil.getTokenRemainingTime(token) / 1000 + ThreadLocalRandom.current().nextInt(5, 30);

                        return redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(expireTime))
                                .then(addHeadersAndContinue(exchange, chain, userInfoMap));
                    } catch (JsonProcessingException e) {
                        log.error("序列化用户信息失败", e);
                        return unauthorizedResponse(exchange.getResponse(), "用户信息处理失败", StatusCode.BUSINESS_EXCEPTION);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> refreshTokenFlow(uuid, exchange, chain)));
    }

    private Mono<Void> refreshTokenFlow(String uuid, ServerWebExchange exchange, GatewayFilterChain chain) {
        return webClientService.sendGet(
                        "http://user-service/user/access_token/" + uuid,
                        new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
                        })
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)))
                .flatMap(resp -> {
                    if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS) || resp.getData() == null) {
                        return unauthorizedResponse(exchange.getResponse(), "查询用户信息失败", StatusCode.SELECT_ERR);
                    }

                    AuthUserDTO authUserDTO = resp.getData();
                    String refreshTokenLockKey = RedisHeaders.REFRESH_TOKEN_LOCK + authUserDTO.getId();

                    return redisTemplate.opsForValue().set(
                                    refreshTokenLockKey, "1", Duration.ofSeconds(5))
                            .filter(Boolean.TRUE::equals)
                            .flatMap(lock -> processTokenRefresh(authUserDTO, exchange, chain, refreshTokenLockKey))
                            .switchIfEmpty(unauthorizedResponse(exchange.getResponse(), "令牌刷新中请重试", StatusCode.TOKEN_EXPIRE_EXCEPTION));
                });
    }

    private Mono<Void> processTokenRefresh(AuthUserDTO authUserDTO, ServerWebExchange exchange,
                                           GatewayFilterChain chain, String lockKey) {
        return Mono.usingWhen(
                Mono.just(lockKey),
                key -> checkRefreshToken(authUserDTO, exchange, chain),
                key -> redisTemplate.delete(key).then()
        );
    }

    private Mono<Void> checkRefreshToken(AuthUserDTO authUserDTO, ServerWebExchange exchange,
                                         GatewayFilterChain chain) {
        String refreshTokenKey = RedisHeaders.REFRESH_TOKEN + authUserDTO.getId();

        return redisTemplate.opsForValue().get(refreshTokenKey)
                .filter(refreshTokenCache -> refreshTokenCache.equals(authUserDTO.getRefreshToken()))
                .flatMap(refreshToken -> generateNewToken(authUserDTO, exchange, chain))
                .switchIfEmpty(unauthorizedResponse(exchange.getResponse(), "刷新令牌无效", StatusCode.TOKEN_EXPIRE_EXCEPTION));
    }

    private Mono<Void> generateNewToken(AuthUserDTO authUserDTO, ServerWebExchange exchange,
                                        GatewayFilterChain chain) {
        String newAccessToken = UUID.randomUUID().toString();
        String newToken = JwtUtil.createToken(
                authUserDTO.getId(),
                authUserDTO.getUsername(),
                JsonUtils.toJson(authUserDTO.getRoles()),
                JsonUtils.toJson(authUserDTO.getPermissions())
        );

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setId(authUserDTO.getId());
        tokenDTO.setAccessToken(newAccessToken);

        return webClientService.sendPut("http://user-service/user/update", tokenDTO,
                        new ParameterizedTypeReference<Resp<String>>() {
                        })
                .flatMap(updateResp -> {
                    System.out.println(updateResp);
                    if (!Objects.equals(updateResp.getCode(), StatusCode.UPDATE_SUCCESS)) {
                        return unauthorizedResponse(exchange.getResponse(), updateResp.getMsg(), updateResp.getCode());
                    }

                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add("Access-Control-Expose-Headers", "access_token");
                    response.getHeaders().add("access_token", newAccessToken);

                    return redisTemplate.opsForValue().set(
                                    RedisHeaders.ACCESS_TOKEN + newAccessToken,
                                    newToken,
                                    Duration.ofMillis(JwtUtil.EXPIRE_TIME))
                            .then(redisTemplate.delete(RedisHeaders.ACCESS_TOKEN + updateResp.getData()))
                            .then(addHeadersAndContinue(exchange, chain, JwtUtil.parseToken(newToken)));
                });
    }

    // 其他方法保持不变...
    private Mono<Void> addHeadersAndContinue(ServerWebExchange exchange,
                                             GatewayFilterChain chain,
                                             Map<String, String> userInfoMap) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(SecurityHeaders.USERID, Optional.ofNullable(userInfoMap.get("userid")).orElse(""))
                .header(SecurityHeaders.USERNAME, Optional.ofNullable(userInfoMap.get("username")).orElse(""))
                .header(SecurityHeaders.ROLES, Optional.ofNullable(userInfoMap.get("roles")).orElse(""))
                .header(SecurityHeaders.PERMISSIONS, Optional.ofNullable(userInfoMap.get("permissions")).orElse(""))
                .header(SecurityHeaders.AUTHENTICATED, "true")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractToken(
            org.springframework.http.server.reactive.ServerHttpRequest request) {
        return request.getHeaders().getFirst("ACCESS_TOKEN");
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg, int code) {
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("msg", msg);
        resp.put("code", code);
        resp.put("current_time", LocalDateTime.now());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -20;
    }

//    public static Mono<Void> webFluxResp(ServerHttpResponse response, String msg, int code) {
//        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
//        HashMap<String, Object> resp = new HashMap<>();
//        resp.put("msg", msg);
//        resp.put("code", code);
//        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes());
//        return response.writeWith(Mono.just(dataBuffer));
//    }

}

