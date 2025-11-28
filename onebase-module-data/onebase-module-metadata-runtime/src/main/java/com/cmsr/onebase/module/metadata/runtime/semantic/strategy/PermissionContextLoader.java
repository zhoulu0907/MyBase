package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.service.permission.builder.PermissionContextBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class PermissionContextLoader {

    @Resource
    private PermissionContextBuilder permissionContextBuilder;

    public void loadPermissionContext(RecordDTO record) {
        Long ctxMenuId = record.getContext().getMenuId();
        RuntimeLoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        LoginUserCtx loginUserCtx = new LoginUserCtx(loginUser.getId(), loginUser.getApplicationId());
        Long entityId = record.getEntity().getId();
        MetadataPermissionContext pc = permissionContextBuilder.buildPermissionContext(loginUserCtx, ctxMenuId, entityId);
        record.getContext().setLoginUserCtx(loginUserCtx);
        record.getContext().setPermissionContext(pc);
    }
}

