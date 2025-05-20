package common.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.utils.HmacUtils;
import common.core.utils.JsonUtils;
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
import java.util.Objects;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

//        放行预检请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        log.info(request.getRequestURI());

        String timestamp = request.getHeader(SecurityHeaders.TIMESTAMP);
        String username = request.getHeader(SecurityHeaders.USERNAME);
        String userId = request.getHeader(SecurityHeaders.USERID);
        String roles = request.getHeader(SecurityHeaders.ROLES);
        String perms = request.getHeader(SecurityHeaders.PERMISSIONS);
        String signature = request.getHeader(SecurityHeaders.SIGNATURE);

//        if (!hasAuthHeaders(username, userId, roles, signature, timestamp)) {
//            respMsg(response, "请求信息缺失", StatusCode.INTERCEPTOR_ERROR);
//            SecurityContextHolder.clearContext();
//            return;
//        }

        if (!hasAuthHeaders(username, userId, roles, signature, timestamp)) {
            respMsg(response, "请求信息缺失", StatusCode.INTERCEPTOR_ERROR);
            SecurityContextHolder.clearContext();
            return;
        }

//        一分钟后请求超时
        try {
            long reqTime = Long.parseLong(timestamp);
            if (System.currentTimeMillis() - reqTime > 60 * 1000) {
                respMsg(response, "访问超时", StatusCode.INTERCEPTOR_ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            respMsg(response, "非法时间格式", StatusCode.INTERCEPTOR_ERROR);
            return;
        }


//        构建签名并验证
        String signatureStr = "userid=" + userId + "&username=" + username + "&timestamp=" + timestamp;
        try {
            boolean isValid = HmacUtils.verifyHmac(signature, signatureStr);
            if (!isValid) {
                respMsg(response, "签名不正确", StatusCode.INTERCEPTOR_ERROR);
                return;
            }
        } catch (Exception e) {
            respMsg(response, "签名校验异常", StatusCode.INTERCEPTOR_ERROR);
            return;
        }

        try {
            log.info(username + " => " + userId + " => " + roles + " => " + perms);
            Authentication auth = buildAuthentication(username, roles, perms);
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }

    }

    private Authentication buildAuthentication(String username,
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
    private boolean hasAuthHeaders(String username, String userId, String roles, String signature, String timestamp) {
        return StringUtils.hasText(username)
                && StringUtils.hasText(userId)
                && StringUtils.hasText(roles)
                && StringUtils.hasText(signature)
                && StringUtils.hasText(timestamp);
    }

    public void respMsg(HttpServletResponse response, String msg, int code) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String asString = objectMapper.writeValueAsString(Resp.error(msg, code));
        response.getWriter().write(asString);
    }
}

