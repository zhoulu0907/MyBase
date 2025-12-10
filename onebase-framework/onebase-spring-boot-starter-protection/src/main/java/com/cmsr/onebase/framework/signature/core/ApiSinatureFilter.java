package com.cmsr.onebase.framework.signature.core;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.signature.core.aop.ApiSignHelper;
import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

@Slf4j
public class ApiSinatureFilter extends OncePerRequestFilter {

    @Resource
    private ApiSignHelper apiSignHelper;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // 检查当前请求的处理器是否添加了@ApiSignIgnore注解
            if (shouldVerifySignature(request)) {
                // 如果有@ApiSignIgnore注解，则跳过签名校验
                // API签名校验，有问题会抛出异常中断
                apiSignHelper.verifySignature(request);
            }
        } catch (ServiceException e) {
            log.error("[doFilterInternal][许可证校验异常]", e);
            CommonResult<?> result = CommonResult.error(e.getCode(), e.getMessage());
            ServletUtils.writeJSON(response, result);
            return; // 中断
        } catch (Exception e) {
            log.error("[doFilterInternal][许可证校验异常，未知异常]", e);
            CommonResult<?> result = CommonResult.error(BAD_REQUEST.getCode(), "请求签名异常");
            ServletUtils.writeJSON(response, result);
            return; // 中断
        }
        // 未中断则继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 判断当前请求是否需要进行API签名校验
     * 如果Controller类或方法上有@ApiSignIgnore注解，则不需要校验
     *
     * @param request 当前HTTP请求
     * @return 是否需要校验签名
     */
    private boolean shouldVerifySignature(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null) {
                return true; // 如果找不到处理器，仍然进行签名校验
            }

            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();

            // 检查方法上是否有@ApiSignIgnore注解
            if (handlerMethod.hasMethodAnnotation(ApiSignIgnore.class)) {
                return false;
            }

            // 检查类上是否有@ApiSignIgnore注解
            if (handlerMethod.getBeanType().isAnnotationPresent(ApiSignIgnore.class)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("[shouldVerifySignature] 获取请求处理器时发生异常", e);
            return true; // 出现异常时，默认进行签名校验
        }
    }
}