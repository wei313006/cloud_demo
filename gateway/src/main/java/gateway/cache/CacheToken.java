//package gateway.cache;
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.github.benmanes.caffeine.cache.LoadingCache;
//import gateway.clients.AuthClient;
//import jakarta.annotation.Resource;
//import org.springframework.stereotype.Component;
//import security.entity.AuthUserDTO;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author abing
// * @created 2025/4/18 16:17
// * token缓存工具类
// */
//public class CacheToken {
//
//    private final AuthClient authClient;
//
//    private final LoadingCache<String, AuthUserDTO> cache;
//
//    //    初始化
//    public CacheToken(AuthClient authClients) {
//        System.out.println(authClients.toString());
//        this.authClient = authClients;
//        this.cache = Caffeine.newBuilder()
////                过期时间5分钟
//                .expireAfterWrite(5, TimeUnit.MINUTES)
//                .maximumSize(10_000)
//                .build(this::loadUserInfoFromAuthService);
//    }
//
//    public AuthUserDTO getUserInfo(String uuid) {
//        try {
//            return cache.get(uuid);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private AuthUserDTO loadUserInfoFromAuthService(String uuid) {
//        // Feign 请求 auth-service，获取用户信息
//        AuthUserDTO authUserDTO = authClient.getUserInfoByUuid(uuid);
//        System.out.println("loadUserInfoFromAuthService => " + authUserDTO);
//        return authUserDTO;
//    }
//
//    public void invalidate(String uuid) {
//        cache.invalidate(uuid);
//    }
//}
