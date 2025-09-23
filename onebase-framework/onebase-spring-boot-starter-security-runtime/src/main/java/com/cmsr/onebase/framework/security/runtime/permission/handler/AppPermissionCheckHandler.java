package com.cmsr.onebase.framework.security.runtime.permission.handler;

import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.app.api.permission.AppPermissionApi;
import com.cmsr.onebase.module.app.api.permission.AppPermissionCheckDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * App动态权限校验
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Slf4j
@Component
public class AppPermissionCheckHandler {


    @Resource
    private AppPermissionApi appPermissionApi;

    /**
     * 静态方法：检查系统 License 是否存在且未过期（向后兼容，便于在无法注入 Bean 的场景使用）。
     *
     * @throws RuntimeException 当没有启用的凭证或凭证已过期时，抛出带有对应错误码的业务异常
     */
    public Boolean checkAppPermission(AppPermissionCheckDTO permissionCheckDTO) {
        Boolean hasPermission = false;
        try {
            hasPermission = appPermissionApi.checkPermssion(permissionCheckDTO);
        } catch (Exception e) {
            log.error("checkAppPermission error.", e);
            throw exception(GlobalErrorCodeConstants.APP_PERM_CHECK_ERROR);
        }
        return hasPermission;

    }

}