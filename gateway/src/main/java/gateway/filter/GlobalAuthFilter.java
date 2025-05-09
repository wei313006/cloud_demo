package gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.RedisHeaders;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.utils.JsonUtils;
import common.security.entity.AuthUserDTO;
import common.security.entity.TokenDTO;
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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author abing
 * @created 2025/4/16 16:42
 * 全局过滤器
 */

@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {


//    @Resource
//    private AuthClient authClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private WebClientService webClientService;

    @Resource
    private ObjectMapper objectMapper;

    private static final List<String> whiteList = List.of("/auth/login", "/user/register");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
//        request.mutate().header(SecurityHeaders.AUTHENTICATED, "true");
        if (HttpMethod.OPTIONS.matches(request.getMethod().toString())) {
            return chain.filter(exchange);
        }

        if (whiteList.stream().anyMatch(request.getURI().getPath()::startsWith)) {
            return chain.filter(exchange);
        }

        String uuid = extractToken(exchange.getRequest());

        if (!StringUtils.hasText(uuid)) {
            return unauthorizedResponse(exchange.getResponse(), "缺少访问令牌", StatusCode.UNKNOWN_EXCEPTION);
        }

        String cacheKey = RedisHeaders.GATEWAY_CACHE + uuid;
        String parsedUserInfo = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.hasText(parsedUserInfo)) {
            try {
                Map<String, String> userInfoMap = objectMapper.readValue(parsedUserInfo,
                        new TypeReference<Map<String, String>>() {
                        });
                return addHeadersAndContinue(exchange, chain, userInfoMap);
            } catch (JsonProcessingException e) {
                log.error("解析缓存用户信息失败", e);
                redisTemplate.delete(cacheKey);
            }
        }

        // 缓存未命中，解析令牌
        String token = redisTemplate.opsForValue().get(RedisHeaders.ACCESS_TOKEN + uuid);
        if (StringUtils.hasText(token)) {
            if (!JwtUtil.verifyToken(token)) {
                redisTemplate.delete(RedisHeaders.ACCESS_TOKEN + uuid);
                return unauthorizedResponse(exchange.getResponse(), "令牌已过期或不可用", StatusCode.TOKEN_EXPIRE_EXCEPTION);
            }

            Map<String, String> userInfoMap = JwtUtil.parseToken(token);
            if (userInfoMap.isEmpty()) {
                return unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION);
            }

            try {
                // 使用原子操作和随机偏移量设置缓存
                String json = objectMapper.writeValueAsString(userInfoMap);
                redisTemplate.opsForValue()
                        .setIfAbsent(cacheKey, json,
                                JwtUtil.getTokenRemainingTime(token) / 1000 + ThreadLocalRandom.current().nextInt(5, 30)
                                , TimeUnit.SECONDS);
//            if (Boolean.TRUE.equals(success)) {
//                log.info("用户信息缓存成功");
//            } else {
//                log.info("已缓存用户信息");
//            }
            } catch (JsonProcessingException e) {
                log.error("序列化用户信息失败", e);
                return unauthorizedResponse(exchange.getResponse(), "用户信息处理失败", StatusCode.BUSINESS_EXCEPTION);
            }
            return addHeadersAndContinue(exchange, chain, userInfoMap);
        } else {
//            token过期，刷新令牌
            return webClientService.sendGet("http://user-service/user/access_token/" + uuid, new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
                    })
                    .flatMap(resp -> {
                        log.warn(String.valueOf(resp));
                        if (Objects.isNull(resp)) {
                            return unauthorizedResponse(exchange.getResponse(), "查询用户信息失败", StatusCode.SELECT_ERR);
                        }
                        if (Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS)) {
                            AuthUserDTO authUserDTO = resp.getData();
                            if (Objects.nonNull(authUserDTO)) {
//                                防止重复创建token
                                String refreshTokenLockKey = RedisHeaders.REFRESH_TOKEN_LOCK + authUserDTO.getId();
                                Boolean lock = redisTemplate.opsForValue().setIfAbsent(refreshTokenLockKey, "1", 5, TimeUnit.SECONDS);
                                if (Boolean.TRUE.equals(lock)) {
                                    try {
                                        String refreshTokenCache = redisTemplate.opsForValue().get(RedisHeaders.REFRESH_TOKEN + authUserDTO.getId());
                                        String refreshToken = authUserDTO.getRefreshToken();
                                        if (StringUtils.hasText(refreshTokenCache) && Objects.equals(refreshTokenCache, refreshToken)) {
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
//                                        发送请求更新accessToken
                                            return webClientService.sendPut("http://user-service/user/update", tokenDTO, new ParameterizedTypeReference<Resp<String>>() {
                                                    })
                                                    .flatMap(updateResp -> {
                                                        if (updateResp.getCode().equals(StatusCode.UPDATE_SUCCESS)) {
                                                            redisTemplate.opsForValue().set(RedisHeaders.ACCESS_TOKEN + newAccessToken, newToken, JwtUtil.EXPIRE_TIME, TimeUnit.MILLISECONDS);
                                                            ServerHttpResponse response = exchange.getResponse();
                                                            response.getHeaders().add("Access-Control-Expose-Headers", "access_token");
                                                            response.getHeaders().add("access_token", newAccessToken);
                                                            Map<String, String> userInfoMap = JwtUtil.parseToken(newToken);
                                                            redisTemplate.delete(RedisHeaders.ACCESS_TOKEN + updateResp.getData());
                                                            return addHeadersAndContinue(exchange, chain, userInfoMap);
                                                        } else {
                                                            return unauthorizedResponse(exchange.getResponse(), "令牌生成失败", StatusCode.UPDATE_ERR);
                                                        }
                                                    }).onErrorResume(e -> {
                                                        log.error("更新用户信息失败", e);
                                                        return unauthorizedResponse(exchange.getResponse(), "更新用户信息失败", StatusCode.UPDATE_ERR);
                                                    }).timeout(Duration.ofSeconds(5))
                                                    .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
                                        }
                                    } finally {
                                        redisTemplate.delete(refreshTokenLockKey);
                                    }
                                }
                            }
                        }
                        return unauthorizedResponse(exchange.getResponse(), "等待令牌刷新", StatusCode.TOKEN_EXPIRE_EXCEPTION);
                    }).onErrorResume(e -> {
                        log.error("尝试通过accessToken获取用户信息失败 => " + e);
                        return unauthorizedResponse(exchange.getResponse(), "查询用户信息失败", StatusCode.SELECT_ERR);
                    }).timeout(Duration.ofSeconds(5))
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
//            redisTemplate.opsForValue().get("refresh_token:" + resp)
//            return unauthorizedResponse(exchange.getResponse(), "令牌不存在", 404);
        }
    }

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

