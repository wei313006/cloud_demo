//package gateway.filter;
//
//import com.alibaba.fastjson2.JSON;
//import common.core.entity.RedisHeaders;
//import common.core.entity.Resp;
//import common.core.entity.StatusCode;
//import common.core.entity.dto.AuthUserDTO;
//import common.core.entity.dto.TokenDTO;
//import common.core.utils.JsonUtils;
//import common.security.entity.SecurityHeaders;
//import common.security.utils.JwtUtil;
//import gateway.clients.WebClientService;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import reactor.util.retry.Retry;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * @author abing
// * @created 2025/4/16 16:42
// * 全局过滤器
// */
//
//@Slf4j
//@Component
//public class GlobalAuthFilterReacticeRedis implements GlobalFilter, Ordered {
//
//    @Autowired
//    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Resource
//    private WebClientService webClientService;
//
//    private static final List<String> whiteList = List.of("/auth/login", "/user/register");
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        // 预检请求直接放行
//        if (HttpMethod.OPTIONS.matches(request.getMethod().toString())) {
//            return chain.filter(exchange);
//        }
//
//        // 白名单检查
//        if (whiteList.stream().anyMatch(request.getURI().getPath()::startsWith)) {
//            return chain.filter(exchange);
//        }
//
//        String uuid = extractToken(request);
//        if (!StringUtils.hasText(uuid)) {
//            return unauthorizedResponse(exchange.getResponse(), "缺少访问令牌", StatusCode.UNKNOWN_EXCEPTION);
//        }
//
//        String cacheKey = RedisHeaders.GATEWAY_CACHE + uuid;
//
//        return reactiveStringRedisTemplate.opsForValue().get(cacheKey)
//                .flatMap(parsedUserInfo -> {
//                    if (!StringUtils.hasText(parsedUserInfo)) {
//                        return checkTokenAndRefresh(uuid, exchange, chain, cacheKey);
//                    } else {
//                        try {
////                        Map<String, String> userInfoMap = objectMapper.readValue(
////                                parsedUserInfo, new TypeReference<Map<String, String>>() {
////                                }
////                        );
//                            Map<String, String> userInfoMap = JsonUtils.toBean(parsedUserInfo, Map.class);
////                        传递已缓存的信息到其它服务
//                            return addHeadersAndContinue(exchange, chain, Optional.ofNullable(userInfoMap.get("userid")).orElse(""));
//                        } catch (Exception e) {
//                            log.error("解析缓存用户信息失败", e);
//                            return reactiveStringRedisTemplate.delete(cacheKey)
//                                    .then(
//                                            unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION)
//                                    );
//                        }
//                    }
//                })
//                .onErrorResume(e -> {
//                    log.error("认证流程异常", e);
//                    return unauthorizedResponse(exchange.getResponse(), "系统异常", StatusCode.UNKNOWN_EXCEPTION);
//                })
//                .doOnError(err -> log.error("Redis 查询异常", err))
//                ;
//    }
//
//    private Mono<Void> checkTokenAndRefresh(String uuid, ServerWebExchange exchange, GatewayFilterChain chain, String cacheKey) {
//        String accessTokenKey = RedisHeaders.ACCESS_TOKEN + uuid;
//
//        return reactiveStringRedisTemplate.opsForValue().get(accessTokenKey)
//                .doOnNext(token -> log.warn("Redis 返回 token: {}", token))
//                .flatMap(token -> {
//
////                token过期不存在，刷新token
//                    if (!StringUtils.hasText(token)) {
//                        return refreshTokenFlow(uuid, exchange, chain);
//                    } else {
//                        log.warn("Redis 返回的 token 是: {}1", token);
//                        if (!JwtUtil.verifyToken(token)) {
//                            return reactiveStringRedisTemplate.delete(accessTokenKey)
//                                    .then(unauthorizedResponse(exchange.getResponse(), "令牌已过期或不可用", StatusCode.TOKEN_EXCEPTION));
//                        }
//                        log.warn("Redis 返回的 token 是: {}2", token);
//
//                        Map<String, String> userInfoMap = JwtUtil.parseToken(token);
//                        if (userInfoMap.isEmpty()) {
//                            return unauthorizedResponse(exchange.getResponse(), "用户信息解析失败", StatusCode.BUSINESS_EXCEPTION);
//                        }
//                        log.warn("Redis 返回的 token 是: {}3", token);
//
////                    缓存解析后的token信息
//                        String json = JsonUtils.toJson(userInfoMap);
//                        long expireTime = JwtUtil.getTokenRemainingTime(token) / 1000 + ThreadLocalRandom.current().nextInt(5, 30);
//
//                        return reactiveStringRedisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(expireTime))
//                                .then(addHeadersAndContinue(exchange, chain, Optional.ofNullable(userInfoMap.get("userid")).orElse("")));
//                    }
//                });
//
//    }
//
//    private Mono<Void> refreshTokenFlow(String uuid, ServerWebExchange exchange, GatewayFilterChain chain) {
//        return webClientService.sendGet(
//                        "http://user-service/user/access_token/" + uuid,
//                        new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
//                        })
//                .flatMap(resp -> {
//                    log.warn(String.valueOf(resp));
//                    if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS) || resp.getData() == null) {
//                        return unauthorizedResponse(exchange.getResponse(), "查询用户信息失败", StatusCode.SELECT_ERR);
//                    }
//
//                    AuthUserDTO authUserDTO = resp.getData();
//                    String refreshTokenLockKey = RedisHeaders.REFRESH_TOKEN_LOCK + authUserDTO.getId();
//
//                    return reactiveStringRedisTemplate.opsForValue().setIfAbsent(
//                                    refreshTokenLockKey, "1", Duration.ofSeconds(5))
//                            .defaultIfEmpty(false) // 默认 false，而不是空 Mono
//                            .flatMap(acquired -> {
//                                if (!acquired) {
//                                    return unauthorizedResponse(exchange.getResponse(), "令牌刷新中请重试", StatusCode.TOKEN_EXCEPTION);
//                                }
//                                return processTokenRefresh(authUserDTO, exchange, chain, refreshTokenLockKey);
//                            });
//                })
//                .timeout(Duration.ofSeconds(5))
//                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
//    }
//
//
//    private Mono<Void> processTokenRefresh(AuthUserDTO authUserDTO, ServerWebExchange exchange,
//                                           GatewayFilterChain chain, String lockKey) {
//        return Mono.usingWhen(
//                Mono.just(lockKey),
//                key -> checkRefreshToken(authUserDTO, exchange, chain),
//                key -> reactiveStringRedisTemplate.delete(key).then()
//        );
//    }
//
//    private Mono<Void> checkRefreshToken(AuthUserDTO authUserDTO, ServerWebExchange exchange, GatewayFilterChain chain) {
//        String refreshTokenKey = RedisHeaders.REFRESH_TOKEN + authUserDTO.getId();
//
//        return reactiveStringRedisTemplate.opsForValue().get(refreshTokenKey)
//                .defaultIfEmpty("") // 防止 empty Mono
//                .flatMap(cachedToken -> {
//                    if (!cachedToken.equals(authUserDTO.getRefreshToken())) {
//                        return unauthorizedResponse(exchange.getResponse(), "刷新令牌无效", StatusCode.TOKEN_EXCEPTION);
//                    }
//                    return generateNewToken(authUserDTO, exchange, chain);
//                });
//    }
//
//
//    private Mono<Void> generateNewToken(AuthUserDTO authUserDTO, ServerWebExchange exchange,
//                                        GatewayFilterChain chain) {
//        String newAccessToken = UUID.randomUUID().toString();
//        String newToken = JwtUtil.createToken(
//                authUserDTO.getId(),
//                authUserDTO.getUsername()
//        );
//
//        TokenDTO tokenDTO = new TokenDTO();
//        tokenDTO.setId(authUserDTO.getId());
//        tokenDTO.setAccessToken(newAccessToken);
//
//        return webClientService.sendPut("http://user-service/user/update", tokenDTO,
//                        new ParameterizedTypeReference<Resp<String>>() {
//                        })
//                .flatMap(updateResp -> {
//                    if (!Objects.equals(updateResp.getCode(), StatusCode.UPDATE_SUCCESS)) {
//                        return unauthorizedResponse(exchange.getResponse(), updateResp.getMsg(), updateResp.getCode());
//                    }
//
//                    ServerHttpResponse response = exchange.getResponse();
//                    response.getHeaders().add("Access-Control-Expose-Headers", "access_token");
//                    response.getHeaders().add("access_token", newAccessToken);
//
//                    return reactiveStringRedisTemplate.opsForValue().set(
//                                    RedisHeaders.ACCESS_TOKEN + newAccessToken,
//                                    newToken,
//                                    Duration.ofMillis(JwtUtil.EXPIRE_TIME))
////                            设置新网关缓存时长、删除旧token和网关缓存
//                            .then(reactiveStringRedisTemplate.opsForValue().set(
//                                    RedisHeaders.GATEWAY_CACHE + newAccessToken,
//                                    JsonUtils.toJson(JwtUtil.parseToken(newToken)),
//                                    Duration.ofSeconds(JwtUtil.getTokenRemainingTime(newToken) / 1000 + ThreadLocalRandom.current().nextInt(5, 30))
//                            ))
//                            .then(reactiveStringRedisTemplate.delete(RedisHeaders.ACCESS_TOKEN + updateResp.getData()))
//                            .then(reactiveStringRedisTemplate.delete(RedisHeaders.GATEWAY_CACHE + updateResp.getData()))
//                            .then(addHeadersAndContinue(exchange, chain, authUserDTO.getId()));
//                }).timeout(Duration.ofSeconds(5))
//                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
//    }
//
//    private Mono<Void> addHeadersAndContinue(ServerWebExchange exchange,
//                                             GatewayFilterChain chain,
//                                             String id) {
//        if (!StringUtils.hasText(id)) {
//            return unauthorizedResponse(exchange.getResponse(), "令牌不合法", StatusCode.TOKEN_EXCEPTION);
//        }
//        return webClientService.sendGet("http://user-service/user/generate/role_perm/" + id,
//                        new ParameterizedTypeReference<Resp<AuthUserDTO>>() {
//                        })
//                .flatMap(resp -> {
//                    log.warn(resp.toString());
//                    if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS) || Objects.isNull(resp.getData())) {
//                        return unauthorizedResponse(exchange.getResponse(), resp.getMsg(), resp.getCode());
//                    }
//                    AuthUserDTO authUserDTO = resp.getData();
//                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                            .header(SecurityHeaders.USERID, authUserDTO.getId())
//                            .header(SecurityHeaders.USERNAME, authUserDTO.getUsername())
//                            .header(SecurityHeaders.ROLES, JsonUtils.toJson(authUserDTO.getRoles()))
//                            .header(SecurityHeaders.PERMISSIONS, JsonUtils.toJson(authUserDTO.getPermissions()))
//                            .header(SecurityHeaders.AUTHENTICATED, "true")
//                            .build();
//
//                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
//                }).timeout(Duration.ofSeconds(5))
//                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
//    }
//
//    private String extractToken(
//            ServerHttpRequest request) {
//        return request.getHeaders().getFirst("access_token");
//    }
//
//    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg, int code) {
//        if (response.isCommitted()) {
//            return response.setComplete(); // 防止抛异常
//        }
//        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
//        HashMap<String, Object> resp = new HashMap<>();
//        resp.put("msg", msg);
//        resp.put("code", code);
//        resp.put("current_time", LocalDateTime.now());
//        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes());
//        return response.writeWith(Mono.just(dataBuffer));
//    }
//
//    @Override
//    public int getOrder() {
//        return -20;
//    }
//
////    public static Mono<Void> webFluxResp(ServerHttpResponse response, String msg, int code) {
////        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
////        HashMap<String, Object> resp = new HashMap<>();
////        resp.put("msg", msg);
////        resp.put("code", code);
////        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes());
////        return response.writeWith(Mono.just(dataBuffer));
////    }
//
//}
//
