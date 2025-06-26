package common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.utils.JsonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("WWW-Authenticate", "");
        String asString = JsonUtils.toJson(Resp.error("没有权限", StatusCode.INTERCEPTOR_ERROR));
        response.getWriter().write(asString);
    }
}
