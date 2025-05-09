package file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import common.security.config.BaseSecurityConfig;

/**
 * @author abing
 * @created 2025/4/29 18:13
 */

@Configuration
@Import(BaseSecurityConfig.class)
public class SecurityConfig {
}
