package gateway.config;

/**
 * @author abing
 * @created 2025/3/27 17:00
 */

import common.security.entity.SecurityHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                        .pathMatchers("/user/admin/**").authenticated()
                        .anyExchange().permitAll()
                )
                .build();
    }

    /**
     * 自定义授权管理器，与过滤器逻辑联动
     */
    private ReactiveAuthorizationManager<AuthorizationContext> authorizationManager() {
        return (mono, context) -> mono
                .flatMap(request -> {
                    ServerWebExchange exchange = context.getExchange();
                    HttpHeaders headers = exchange.getRequest().getHeaders();

                    // 从请求头中直接获取签名
                    boolean isAuthenticated = headers.containsKey(SecurityHeaders.SIGNATURE);
                    return Mono.just(new AuthorizationDecision(isAuthenticated));
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
