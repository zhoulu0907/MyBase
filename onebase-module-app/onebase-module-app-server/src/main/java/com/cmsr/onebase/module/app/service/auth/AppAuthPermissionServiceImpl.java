package com.cmsr.onebase.module.app.service.auth;

import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionRespVO;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:57
 */
@Setter
@Service
public class AppAuthPermissionServiceImpl implements AppAuthPermissionService {

    @Override
    public AuthRolePermissionRespVO getRolePermission(Long roleId, Long menuId) {
        return null;
    }

    @Override
    public Boolean updateRolePermission(AuthRolePermissionReqVO reqVO) {
        return null;
    }
}
