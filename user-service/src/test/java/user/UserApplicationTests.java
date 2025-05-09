package user;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import user.entity.RolePermissions;
import user.service.RolePermissionsService;

import java.util.List;

@SpringBootTest
@DirtiesContext
class UserApplicationTests {

    @Resource
    private RolePermissionsService rolePermissionsService;

    @Test
    void contextLoads() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123");
        System.out.println(encode);
    }

}
