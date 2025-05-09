package common.security.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author abing
 * @created 2025/4/16 13:56
 */

@Configuration
@EnableFeignClients(basePackages = "common.security")
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("ACCESS_TOKEN", "token");
    }
}
