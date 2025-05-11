package common.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.Resp;
import common.core.entity.dto.AuthUserDTO;
import common.feign.clients.UserClient;
import common.security.entity.SecurityHeaders;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abing
 * @created 2025/4/18 21:41
 * 配置共有过滤器
 */
@Slf4j
@Component
public class BaseSecurityFilter extends OncePerRequestFilter {

    @Resource
    private ObjectMapper objectMapper;

//    @Resource
//    private UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

//        String sourceFromGateway = request.getHeader(SecurityHeaders.AUTHENTICATED);
//        if (!sourceFromGateway.equals("true")){
//            return;
//        }


        log.info(request.getRequestURI() + " => " + request.getMethod());
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }
        try {
            String username = request.getHeader(SecurityHeaders.USERNAME);
            String userId = request.getHeader(SecurityHeaders.USERID);
            String roles = request.getHeader(SecurityHeaders.ROLES);
            String perms = request.getHeader(SecurityHeaders.PERMISSIONS);
//            Resp<AuthUserDTO> resp = userClient.generate(userId);

            log.warn(username + " => " + userId + " => " + roles + " => " + perms + " findByUid => " );
            if (isValidHeaders(username, userId, roles)) {
//                verifyRequestSource(request); // 校验请求来源
                Authentication auth = buildAuthentication(username, userId, roles, perms);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                SecurityContextHolder.clearContext();
//                throw new InsufficientAuthenticationException("认证头信息缺失");
            }
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Authentication buildAuthentication(String username, String userId,
                                               String roles, String perms) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            List<String> roleList = objectMapper.readValue(roles, new TypeReference<>() {
            });
            roleList.forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

            if (StringUtils.hasText(perms)) {
                List<String> permList = objectMapper.readValue(perms, new TypeReference<>() {
                });
                permList.forEach(perm ->
                        authorities.add(new SimpleGrantedAuthority("PERMISSION_" + perm)));
            }
        } catch (JsonProcessingException e) {
            throw new AuthenticationServiceException("权限解析失败", e);
        }

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    // 自定义校验逻辑
    private boolean isValidHeaders(String username, String userId, String roles) {
        return StringUtils.hasText(username)
                && StringUtils.hasText(userId)
                && StringUtils.hasText(roles);
    }

    private void verifyRequestSource(HttpServletRequest request) {
        // 实现签名/IP校验逻辑
    }
}

