package com.cmsr.onebase.server.flink.filter;

import com.cmsr.onebase.jose.JoseValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/11/12 20:09
 */
@Slf4j
public class RemoteCallAuthenticationFilter extends OncePerRequestFilter {

    public static final String X_EXEC_TOKEN = "X-Exec-Token";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        //解析token的合法性如果不合法，返回认证错误异常
        if (token == null || validateToken(token) == false) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String body = OBJECT_MAPPER.writeValueAsString(Map.of("msg", "认证错误"));
            response.getWriter().write(body);
            return;
        }
        //创建认证信息
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                token, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //
        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        try {
            return JoseValidator.validate(token);
        } catch (Exception e) {
            log.error("token验证失败", e);
            return false;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(X_EXEC_TOKEN);
    }

}
