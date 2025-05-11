package common.feign.clients;

import common.core.entity.Resp;
import common.core.entity.dto.AuthUserDTO;
import common.core.entity.dto.TokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author abing
 * @created 2025/4/16 13:50
 */

@Configuration
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/user/auth/login")
    Resp<AuthUserDTO> login(@RequestBody AuthUserDTO authUserDTO);

    @PutMapping("/user/update")
    Resp<String> update(@RequestBody TokenDTO tokenDTO);

    @GetMapping("/user/generate/role_perm/{id}")
    Resp<AuthUserDTO> generate(@PathVariable String id);
}

