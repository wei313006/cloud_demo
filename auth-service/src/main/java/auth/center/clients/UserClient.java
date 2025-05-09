package auth.center.clients;

import common.core.entity.Resp;
import common.security.entity.TokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import common.security.entity.AuthUserDTO;

/**
 * @author abing
 * @created 2025/4/15 18:04
 */

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/user/auth/login")
    Resp<AuthUserDTO> login(@RequestBody AuthUserDTO authUserDTO);

    @PutMapping("/user/update")
    Resp<String> update(@RequestBody TokenDTO tokenDTO);

}
