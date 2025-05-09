package gateway.config;

/**
 * @author abing
 * @created 2025/3/27 17:00
 */

import common.core.entity.StatusCode;
import common.core.utils.JsonUtils;
import common.security.entity.Resp;
import common.security.entity.SecurityHeaders;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;


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

                    // 从请求头中直接获取认证标记
                    boolean isAuthenticated = headers.containsKey(SecurityHeaders.AUTHENTICATED);
                    return Mono.just(new AuthorizationDecision(isAuthenticated));
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
