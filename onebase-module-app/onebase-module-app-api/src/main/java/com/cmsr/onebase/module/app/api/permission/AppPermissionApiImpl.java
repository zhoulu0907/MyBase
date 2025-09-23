package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author：matianyu
 */
@Setter
@Validated
@Service
public class AppPermissionApiImpl implements AppPermissionApi {

    @Resource
    private AppAuthRoleRepository appAuthRoleRepository;

    @Override
    public Boolean checkPermssion(AppPermissionCheckDTO permissionCheckDTO) {
        return true;
    }
}
