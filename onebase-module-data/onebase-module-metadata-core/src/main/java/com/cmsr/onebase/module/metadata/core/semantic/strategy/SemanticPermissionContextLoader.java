package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.service.permission.builder.PermissionContextBuilder;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SemanticPermissionContextLoader {

    @Resource
    private PermissionContextBuilder permissionContextBuilder;

    public void loadPermissionContext(SemanticRecordDTO record) {
        Long ctxMenuId = record.getRecordContext().getMenuId();
        RuntimeLoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        LoginUserCtx loginUserCtx = new LoginUserCtx(loginUser.getId(), ApplicationManager.getApplicationId());
        Long entityId = record.getEntitySchema().getId();
        MetadataPermissionContext pc = permissionContextBuilder.buildPermissionContext(loginUserCtx, ctxMenuId, entityId);
        record.getRecordContext().setLoginUserCtx(loginUserCtx);
        record.getRecordContext().setPermissionContext(pc);
    }
}
