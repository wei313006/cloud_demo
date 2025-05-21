package gateway.filter;

import com.alibaba.fastjson2.JSON;
import common.core.entity.RedisHeaders;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.entity.dto.AuthUserDTO;
import common.core.utils.AesEncipherUtils;
import common.core.utils.HmacUtils;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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
import reactor.core.scheduler.Schedulers;
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
    private StringRedisTemplate redisTemplate;

    @Resource
    private WebClientService webClientService;

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

//        log.info(request.getURI().getPath() + " => " + request.getURI().getPath().startsWith("/user/admin/"));

//        只拦截这一请求路径开头并进入认证逻辑，@PreAuthorize注解不在认证路径下时会报错
        if (request.getURI().getPath().startsWith("/user/admin/")) {

            String accessToken = extractToken(request);
            if (!StringUtils.hasText(accessToken)) {
                return unauthorizedResponse(exchange.getResponse(), "缺少访问令牌", StatusCode.UNKNOWN_EXCEPTION);
            }

            String cacheKey = RedisHeaders.GATEWAY_CACHE + accessToken;

            String parsedUserInfo = redisTemplate.opsForValue().get(cacheKey);

            if (StringUtils.hasText(parsedUserInfo)) {
                try {
                    Map<String, String> userInfoMap = JsonUtils.toBean(parsedUserInfo, Map.class);
                    return generateHeadersAndContinue(exchange, chain, Optional.ofNullable(userInfoMap.get("userid")).orElse(""));
                } catch (Exception e) {
                    log.error("解析缓存用户信息失败", e);
                    redisTemplate.delete(cacheKey);
                    return unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION);
                }
            } else {
                return checkTokenAndRefresh(accessToken, exchange, chain, cacheKey);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> checkTokenAndRefresh(String accessToken, ServerWebExchange exchange, GatewayFilterChain chain, String cacheKey) {

        String accessTokenKey = RedisHeaders.ACCESS_TOKEN + accessToken;
        String token = redisTemplate.opsForValue().get(accessTokenKey);

        if (!StringUtils.hasText(token)) {
            return refreshTokenFlow(accessToken, exchange, chain);
        } else {
            if (!JwtUtil.verifyToken(token)) {
                redisTemplate.delete(accessTokenKey);
                return refreshTokenFlow(accessToken, exchange, chain);
//                return unauthorizedResponse(exchange.getResponse(), "令牌已过期或不可用", StatusCode.TOKEN_EXCEPTION);
            }

            Map<String, String> userInfoMap = JwtUtil.parseToken(token);
            if (userInfoMap.isEmpty()) {
                return unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION);
            }

            String json = JsonUtils.toJson(userInfoMap);
            long expireTime = JwtUtil.getTokenRemainingTime(token) / 1000 + ThreadLocalRandom.current().nextInt(5, 30);

            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(expireTime));

            return generateHeadersAndContinue(exchange, chain, Optional.ofNullable(userInfoMap.get("userid")).orElse(""));
        }
    }

    private Mono<Void> refreshTokenFlow(String accessToken, ServerWebExchange exchange, GatewayFilterChain chain) {
        return webClientService.sendGet(
                        "http://user-service/user/access_token/" + accessToken,
                        new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
                        })
                .flatMap(resp -> {

                    if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS) || resp.getData() == null) {
                        return unauthorizedResponse(exchange.getResponse(), "查询用户信息失败", StatusCode.SELECT_ERR);
                    }

                    AuthUserDTO authUserDTO = resp.getData();
                    String refreshTokenLockKey = RedisHeaders.REFRESH_TOKEN_LOCK + authUserDTO.getId();

                    boolean acquired = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(refreshTokenLockKey, "1", Duration.ofSeconds(5)));
                    if (!acquired) {
                        return unauthorizedResponse(exchange.getResponse(), "令牌刷新中请重试", StatusCode.TOKEN_EXCEPTION);
                    }

                    return processTokenRefresh(authUserDTO, exchange, chain, refreshTokenLockKey);
                })
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
    }

    private Mono<Void> processTokenRefresh(AuthUserDTO authUserDTO, ServerWebExchange exchange,
                                           GatewayFilterChain chain, String lockKey) {
        return Mono.usingWhen(
                Mono.just(lockKey),
                key -> checkRefreshToken(authUserDTO, exchange, chain),
                key -> {
                    redisTemplate.delete(key);
                    return Mono.empty();
                }
        );
    }

    private Mono<Void> checkRefreshToken(AuthUserDTO authUserDTO, ServerWebExchange exchange, GatewayFilterChain chain) {
        String refreshTokenKey = RedisHeaders.REFRESH_TOKEN + authUserDTO.getId();

        String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

        if (!StringUtils.hasText(refreshToken) || !refreshToken.equals(authUserDTO.getRefreshToken())) {
            return unauthorizedResponse(exchange.getResponse(), "令牌无效", StatusCode.TOKEN_EXCEPTION);
        }

        return generateNewToken(authUserDTO, exchange, chain);
    }

    private Mono<Void> generateNewToken(AuthUserDTO authUserDTO, ServerWebExchange exchange,
                                        GatewayFilterChain chain) {
        String newAccessToken = UUID.randomUUID().toString();
        String newToken = JwtUtil.createToken(
                authUserDTO.getId(),
                authUserDTO.getUsername()
        );

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setId(authUserDTO.getId());
        tokenDTO.setAccessToken(newAccessToken);

        return webClientService.sendPut("http://user-service/user/update", tokenDTO,
                        new ParameterizedTypeReference<Resp<String>>() {
                        })
                .flatMap(updateResp -> {
                    if (!Objects.equals(updateResp.getCode(), StatusCode.UPDATE_SUCCESS)) {
                        return unauthorizedResponse(exchange.getResponse(), updateResp.getMsg(), updateResp.getCode());
                    }

                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add("Access-Control-Expose-Headers", "access_token");
                    response.getHeaders().add("access_token", newAccessToken);

//                    设置token缓存时间并清除旧缓存
                    redisTemplate.opsForValue().set(
                            RedisHeaders.ACCESS_TOKEN + newAccessToken,
                            newToken,
                            Duration.ofMillis(JwtUtil.EXPIRE_TIME));

                    redisTemplate.opsForValue().set(
                            RedisHeaders.GATEWAY_CACHE + newAccessToken,
                            JsonUtils.toJson(JwtUtil.parseToken(newToken)),
                            Duration.ofSeconds(JwtUtil.getTokenRemainingTime(newToken) / 1000 + ThreadLocalRandom.current().nextInt(5, 30))
                    );

                    redisTemplate.delete(RedisHeaders.ACCESS_TOKEN + updateResp.getData());
                    redisTemplate.delete(RedisHeaders.GATEWAY_CACHE + updateResp.getData());

                    return generateHeadersAndContinue(exchange, chain, authUserDTO.getId());
                }).timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
    }


    private Mono<Void> generateHeadersAndContinue(ServerWebExchange exchange,
                                                  GatewayFilterChain chain,
                                                  String id) {
        if (!StringUtils.hasText(id)) {
            return unauthorizedResponse(exchange.getResponse(), "令牌不合法", StatusCode.TOKEN_EXCEPTION);
        }

        String userRolePerm = redisTemplate.opsForValue().get(RedisHeaders.USER_ROLE_PERM_CACHE + id);
        if (StringUtils.hasText(userRolePerm)) {
            AuthUserDTO authUserDTO = JsonUtils.toBean(userRolePerm, AuthUserDTO.class);
            ServerHttpRequest serverHttpRequest = addHeaders(exchange.getRequest(), authUserDTO);
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        }

        return webClientService.sendGet("http://user-service/user/generate/role_perm/" + id,
                        new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
                        })
                .flatMap(resp -> {
                    if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS) || Objects.isNull(resp.getData())) {
                        return unauthorizedResponse(exchange.getResponse(), resp.getMsg(), resp.getCode());
                    }
                    AuthUserDTO authUserDTO = resp.getData();
                    ServerHttpRequest serverHttpRequest = addHeaders(exchange.getRequest(), authUserDTO);

//                    缓存角色权限信息
                    redisTemplate.opsForValue().set(RedisHeaders.USER_ROLE_PERM_CACHE + authUserDTO.getId(), JsonUtils.toJson(authUserDTO), Duration.ofMinutes(5));
                    return chain.filter(exchange.mutate().request(serverHttpRequest).build());
                }).timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
    }

    public ServerHttpRequest addHeaders(ServerHttpRequest serverHttpRequest, AuthUserDTO authUserDTO) {
        long currentTimeMillis = System.currentTimeMillis();
        String signatureStr = "userid=" + authUserDTO.getId() + "&username=" + authUserDTO.getUsername() + "&timestamp=" + currentTimeMillis;
        String signature;
        try {
            signature = HmacUtils.generateHmacSHA256(signatureStr);
        } catch (Exception e) {
            throw new RuntimeException("签名失败 ");
        }
        return serverHttpRequest.mutate()
                .header(SecurityHeaders.USERID, authUserDTO.getId())
                .header(SecurityHeaders.USERNAME, authUserDTO.getUsername())
                .header(SecurityHeaders.ROLES, JsonUtils.toJson(authUserDTO.getRoles()))
                .header(SecurityHeaders.PERMISSIONS, JsonUtils.toJson(authUserDTO.getPermissions()))
                .header(SecurityHeaders.TIMESTAMP, String.valueOf(currentTimeMillis))
//                添加用户信息签名
                .header(SecurityHeaders.SIGNATURE, signature)
                .build();
    }

    private String extractToken(
            org.springframework.http.server.reactive.ServerHttpRequest request) {
        return request.getHeaders().getFirst("access_token");
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg, int code) {
//        防止重复提交
        if (response.isCommitted()) {
            return response.setComplete();
        }
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        response.getHeaders().add("WWW-Authenticate", "");
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

