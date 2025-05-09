package auth.center.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import common.security.config.BaseSecurityConfig;

/**
 * @author abing
 * @created 2025/4/17 16:50
 */

@Configuration
@Import(BaseSecurityConfig.class)
public class SecurityConfig {

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); // 使用 BCrypt 加密
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/user", true)
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
//                        .requestMatchers("/user/admin/**").authenticated()
//                        .anyRequest().permitAll()
//                )
//                .build();
//    }


//    @Bean
//    @Lazy  // 延迟加载打破循环
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config
//    ) throws Exception {
//        // 强制返回原始对象（非代理）
//        return new AuthenticationManagerDelegator(config);
//    }
//
//    // 自定义 Delegator 避免代理
//    private static class AuthenticationManagerDelegator implements AuthenticationManager {
//        private final AuthenticationManager delegate;
//
//        AuthenticationManagerDelegator(AuthenticationConfiguration config) throws Exception {
//            this.delegate = config.getAuthenticationManager();
//        }
//
//        @Override
//        public Authentication authenticate(Authentication authentication) {
//            return delegate.authenticate(authentication);
//        }
//    }

}
