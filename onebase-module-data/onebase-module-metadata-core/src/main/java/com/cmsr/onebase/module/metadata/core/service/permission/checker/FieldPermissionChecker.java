package com.cmsr.onebase.module.metadata.core.service.permission.checker;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionChecker;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 字段权限校验器
 * 
 * 校验用户对特定字段的访问权限，包括：
 * 1. 字段可读权限（canRead）
 * 2. 字段可写权限（canEdit）
 * 3. 字段下载权限（canDownload，针对文件类字段）
 * 
 * 根据操作类型进行不同的校验：
 * - CREATE/UPDATE：检查字段的编辑权限（canEdit）
 * - QUERY/GET/LIST/PAGE：检查字段的读取权限（canRead），并过滤无权读取的字段
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
@Slf4j
@Component
public class FieldPermissionChecker implements PermissionChecker {

    private static final String PERMISSION_TYPE = "字段权限";

    @Override
    public String getPermissionType() {
        return PERMISSION_TYPE;
    }

    @Override
    public boolean supports(ProcessContext context) {
        return context != null 
                && context.getMetadataPermissionContext() != null
                && context.getMetadataPermissionContext().getFieldPermission() != null
                && context.getData() != null
                && !context.getData().isEmpty();
    }

    @Override
    public void check(ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        FieldPermission fieldPermission = permissionContext.getFieldPermission();

        if (fieldPermission == null) {
            log.warn("字段权限对象为空，跳过字段权限校验");
            return;
        }

        // 如果配置了全部拒绝，直接抛出异常
        if (fieldPermission.isAllDenied()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "ALL_DENIED",
                    "无权访问任何字段"
            );
        }

        // 如果配置了全部允许，直接通过
        if (fieldPermission.isAllAllowed()) {
            log.debug("字段权限：全部允许");
            return;
        }

        MetadataDataMethodOpEnum operationType = context.getOperationType();
        Map<String, Object> data = context.getProcessedData() != null 
                ? context.getProcessedData() 
                : context.getData();

        // 根据操作类型进行不同的字段权限校验
        switch (operationType) {
            case CREATE:
            case UPDATE:
                // 写操作：检查字段的编辑权限
                checkFieldEditPermission(fieldPermission, data);
                break;

            case GET:
            case GET_PAGE:
            case GET_PAGE_OR:
                // 读操作：检查字段的读取权限（实际应用中，读权限过滤通常在查询后进行）
                checkFieldReadPermission(fieldPermission, data);
                break;

            default:
                log.debug("操作类型{}不需要进行字段权限校验", operationType);
        }

        log.debug("字段权限校验通过：operationType={}, fieldCount={}", 
                operationType, 
                data.size());
    }

    @Override
    public int getOrder() {
        return 30;
    }

    /**
     * 检查字段的编辑权限
     * 
     * 用于CREATE和UPDATE操作，确保用户只能编辑有权限的字段
     *
     * @param fieldPermission 字段权限对象
     * @param data 数据（key为fieldId，value为字段值）
     */
    private void checkFieldEditPermission(FieldPermission fieldPermission, Map<String, Object> data) {
        List<FieldPermissionItem> fields = fieldPermission.getFields();
        
        if (fields == null || fields.isEmpty()) {
            throw new PermissionDeniedException(
                    PERMISSION_TYPE,
                    "NO_FIELDS",
                    "未配置字段权限"
            );
        }

        // 构建可编辑字段ID集合
        Set<Long> editableFieldIds = new HashSet<>();
        for (FieldPermissionItem field : fields) {
            if (field.isCanEdit()) {
                editableFieldIds.add(field.getFieldId());
            }
        }

        // 检查数据中的每个字段是否有编辑权限
        for (String fieldIdStr : data.keySet()) {
            try {
                Long fieldId = Long.parseLong(fieldIdStr);
                
                if (!editableFieldIds.contains(fieldId)) {
                    throw new PermissionDeniedException(
                            PERMISSION_TYPE,
                            "FIELD_EDIT",
                            String.format("无权编辑字段：fieldId=%s", fieldIdStr)
                    );
                }
            } catch (NumberFormatException e) {
                // 如果字段ID不是数字，可能是系统字段（如created_at等），跳过
                log.debug("跳过非数字字段ID的权限校验：{}", fieldIdStr);
            }
        }
    }

    /**
     * 检查字段的读取权限
     * 
     * 用于查询操作，过滤掉用户无权读取的字段
     * 注意：此方法主要用于校验，实际的字段过滤应该在查询结果处理阶段进行
     *
     * @param fieldPermission 字段权限对象
     * @param data 数据（key为fieldId，value为字段值）
     */
    private void checkFieldReadPermission(FieldPermission fieldPermission, Map<String, Object> data) {
        List<FieldPermissionItem> fields = fieldPermission.getFields();
        
        if (fields == null || fields.isEmpty()) {
            log.warn("未配置字段权限，默认允许读取所有字段");
            return;
        }

        // 构建可读字段ID集合
        Set<Long> readableFieldIds = new HashSet<>();
        for (FieldPermissionItem field : fields) {
            if (field.isCanRead()) {
                readableFieldIds.add(field.getFieldId());
            }
        }

        // 移除无权读取的字段（直接修改data，过滤敏感字段）
        data.keySet().removeIf(fieldIdStr -> {
            try {
                Long fieldId = Long.parseLong(fieldIdStr);
                boolean hasPermission = readableFieldIds.contains(fieldId);
                
                if (!hasPermission) {
                    log.debug("过滤无权读取的字段：fieldId={}", fieldIdStr);
                }
                
                return !hasPermission;
            } catch (NumberFormatException e) {
                // 系统字段保留
                return false;
            }
        });
    }
}

