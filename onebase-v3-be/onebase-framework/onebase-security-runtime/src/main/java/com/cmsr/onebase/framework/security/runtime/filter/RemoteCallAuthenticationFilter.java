package com.cmsr.onebase.framework.security.runtime.filter;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.jose.JoseValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * @Author：huangjie
 * @Date：2025/11/12 20:09
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteCallAuthenticationFilter extends OncePerRequestFilter {

    public static final String X_EXEC_TOKEN = "X-Exec-Token";

    public static final RequestMatcher flowRemoteCallRequestMatcher = new AntPathRequestMatcher("/runtime/flow/remote-call/**");

    private boolean doFilter(HttpServletRequest request) {
        return flowRemoteCallRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!doFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = resolveToken(request);
        //解析token的合法性如果不合法，返回认证错误异常
        if (token == null || validateToken(token) == false) {
            CommonResult<?> result = CommonResult.error(401, "认证错误");
            response.setStatus(401);
            ServletUtils.writeJSON(response, result);
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
