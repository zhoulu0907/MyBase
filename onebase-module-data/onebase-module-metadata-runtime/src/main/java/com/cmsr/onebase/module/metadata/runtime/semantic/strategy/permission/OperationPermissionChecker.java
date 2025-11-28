package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.permission;

import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import org.springframework.stereotype.Component;

/**
 * 操作权限校验器
 *
 * 根据页面权限与操作标签（新增/编辑/删除），
 * 校验用户是否具备对应操作的执行权限。
 */
@Component
public class OperationPermissionChecker implements RuntimePermissionChecker {
    private static final String TYPE = "操作权限";

    @Override
    /**
     * 返回权限类型标识
     */
    public String getPermissionType() { return TYPE; }

    @Override
    /**
     * 操作权限对所有 RecordDTO 都进行校验
     */
    public boolean supports(RecordDTO recordDTO) { return true; }

    @Override
    /**
     * 按操作类型进行权限校验：
     * - 页面访问：isPageAllowed
     * - 新增/编辑/删除：对应 canCreate/canEdit/canDelete
     */
    public void check(RecordDTO recordDTO) {
        MetadataPermissionContext pc = recordDTO.getContext().getPermissionContext();
        OperationPermission op = pc.getOperationPermission();
        MetadataDataMethodOpEnum type = recordDTO.getContext().getOperationType();
        if (!op.isPageAllowed()) { throw new PermissionDeniedException(TYPE, "PAGE_ACCESS", "无权访问页面"); }
        switch (type) {
            case CREATE -> { if (!op.isCanCreate()) throw new PermissionDeniedException(TYPE, "CREATE", "无新增权限"); }
            case UPDATE -> { if (!op.isCanEdit()) throw new PermissionDeniedException(TYPE, "EDIT", "无编辑权限"); }
            case DELETE -> { if (!op.isCanDelete()) throw new PermissionDeniedException(TYPE, "DELETE", "无删除权限"); }
            default -> {}
        }
    }

    @Override
    /**
     * 操作权限校验优先级，低于数据权限与字段权限
     */
    public int getOrder() { return 10; }
}
