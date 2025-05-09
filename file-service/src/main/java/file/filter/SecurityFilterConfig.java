package file.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import common.security.filter.BaseSecurityFilter;

/**
 * @author abing
 * @created 2025/4/29 18:15
 */
@Configuration
@Import(BaseSecurityFilter.class)
public class SecurityFilterConfig {
}
