package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionRespVO;
import jakarta.validation.Valid;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:06
 */
public interface AppAuthPermissionService {

    AuthRolePermissionRespVO getRolePermission(Long roleId, Long menuId);

    Boolean updateRolePermission(@Valid AuthRolePermissionReqVO reqVO);

}
