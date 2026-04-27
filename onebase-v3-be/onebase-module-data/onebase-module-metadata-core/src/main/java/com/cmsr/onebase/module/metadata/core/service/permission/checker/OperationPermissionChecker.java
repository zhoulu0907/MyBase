package com.cmsr.onebase.module.metadata.core.service.permission.checker;

import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionChecker;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 操作权限校验器
 * 
 * 校验用户是否具有对应的功能操作权限（增删改查、导入导出、分享等）
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class OperationPermissionChecker implements PermissionChecker {

    private static final String PERMISSION_TYPE = "操作权限";

    @Override
    public String getPermissionType() {
        return PERMISSION_TYPE;
    }

    @Override
    public boolean supports(ProcessContext context) {
        // 所有操作都需要进行操作权限校验
        return context != null 
                && context.getMetadataPermissionContext() != null
                && context.getMetadataPermissionContext().getOperationPermission() != null;
    }

    @Override
    public void check(ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        OperationPermission operationPermission = permissionContext.getOperationPermission();
        MetadataDataMethodOpEnum operationType = context.getOperationType();

        // 首先检查页面访问权限
        if (!operationPermission.isPageAllowed()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "PAGE_ACCESS",
                    "无权访问该页面或实体"
            );
        }

        // 根据操作类型检查对应的操作权限
        checkOperationTypePermission(operationPermission, operationType);

        log.debug("操作权限校验通过：operationType={}", operationType.getDescription());
    }

    @Override
    public int getOrder() {
        return 10;
    }

    /**
     * 根据操作类型检查对应的权限
     *
     * @param operationPermission 操作权限对象
     * @param operationType 操作类型
     */
    private void checkOperationTypePermission(OperationPermission operationPermission, 
                                               MetadataDataMethodOpEnum operationType) {
        switch (operationType) {
            case CREATE:
                if (!operationPermission.isCanCreate()) {
                    throw new PermissionDeniedException(
                            PERMISSION_TYPE,
                            "CREATE",
                            "无新增权限"
                    );
                }
                break;

            case UPDATE:
                if (!operationPermission.isCanEdit()) {
                    throw new PermissionDeniedException(
                            PERMISSION_TYPE,
                            "EDIT",
                            "无编辑权限"
                    );
                }
                break;

            case DELETE:
                if (!operationPermission.isCanDelete()) {
                    throw new PermissionDeniedException(
                            PERMISSION_TYPE,
                            "DELETE",
                            "无删除权限"
                    );
                }
                break;

            case GET:
            case GET_PAGE:
            case GET_PAGE_OR:
                // 查询类操作只需要页面访问权限
                log.debug("查询类操作，已通过页面访问权限校验");
                break;

            default:
                log.warn("未知的操作类型：{}", operationType);
        }
    }
}

