package common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.utils.JsonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("WWW-Authenticate", "");
        String asString = JsonUtils.toJson(Resp.error("认证失败，账号或者密码错误", StatusCode.AUTHORIZED_EXCEPTION));
        response.getWriter().write(asString);
    }
}