package com.cmsr.onebase.framework.security.runtime.permission.filter;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.security.runtime.permission.handler.AppPermissionCheckHandler;
import com.cmsr.onebase.module.app.api.permission.AppPermissionCheckDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AppPermissionCheckFilter extends OncePerRequestFilter {

    @Resource
    private AppPermissionCheckHandler appPermissionCheckHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // 动态权限校验
            AppPermissionCheckDTO permissionCheckDTO = new AppPermissionCheckDTO();
            // 目标用户：谁？
            permissionCheckDTO.setUserId(SecurityFrameworkUtils.getLoginUserId());
            // 目标应用：哪个应用？
            permissionCheckDTO.setAppId(0L); // TODO 后续动态化
            // 目标权限：哪个权限点？
            permissionCheckDTO.setPermission("perm-code"); // TODO 后续动态化
            // TODO 可根据情形加资源类型，资源ID等信息。

            Boolean hasPermssion = appPermissionCheckHandler.checkAppPermission(permissionCheckDTO);
            if (!hasPermssion) {
                // TODO 返回无权限，需要细化一下提示语等
                CommonResult<?> result = CommonResult.error(-1, "App无权限");
                ServletUtils.writeJSON(response, result);
                return;
            }
        } catch (Exception e) {
            log.error("[doFilterInternal][许可证校验异常，未知异常]", e);
            CommonResult<?> result = CommonResult.error(-1, "App权限校验异常");
            ServletUtils.writeJSON(response, result);
            return;
        }
        // 有权限，则继续过滤链
        filterChain.doFilter(request, response);
    }
}
