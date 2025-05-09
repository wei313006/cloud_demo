//package gateway.clients;
//
////import core.entity.Resp;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import security.entity.AuthUserDTO;
//
///**
// * @author abing
// * @created 2025/4/15 18:43
// * Webflux不适用feign请求
// */
//
//@FeignClient(name = "auth-service")
//public interface AuthClient {
//
//    @GetMapping("/auth/get_user_info_by_uuid/{uuid}")
//    AuthUserDTO getUserInfoByUuid(@PathVariable String uuid);
//
//}
