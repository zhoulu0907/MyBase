package com.cmsr.onebase.framework.license.core.filter;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.license.core.handler.LicenseCheckHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LicenseCheckFilter extends OncePerRequestFilter {

    @Resource
    private LicenseCheckHandler licenseCheckHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // 许可证校验，许可证有问题会抛出异常中断
            licenseCheckHandler.checkLicense();
        } catch (ServiceException e) {
            log.error("[doFilterInternal][许可证校验异常]", e);
            CommonResult<?> result = CommonResult.error(e.getCode(), e.getMessage());
            ServletUtils.writeJSON(response, result);
            return; // 中断
        } catch (Exception e) {
            log.error("[doFilterInternal][许可证校验异常，未知异常]", e);
            CommonResult<?> result = CommonResult.error(-1, "许可证校验异常");
            ServletUtils.writeJSON(response, result);
            return; // 中断
        }
        // 未中断则继续过滤链
        filterChain.doFilter(request, response);
    }
}
