package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.permission;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.permission.exception.PermissionDeniedException;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticValueDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * 字段权限校验器
 *
 * 用于在 CREATE/UPDATE 场景下，基于字段权限配置控制可编辑字段：
 * - 严格模式：遇到不可编辑字段直接抛出异常
 * - 过滤模式：移除不可编辑字段，减少抛错
 */
@Component
public class SemanticFieldPermissionChecker implements SemanticRuntimePermissionChecker {
    private static final String TYPE = "字段权限";
    

    @Override
    public String getPermissionType() { return TYPE; }

    @Override
    /**
     * 仅在 CREATE 或 UPDATE 操作时生效
     */
    public boolean supports(SemanticRecordDTO recordDTO) {
        return recordDTO.getContext().getOperationType() == MetadataDataMethodOpEnum.CREATE
                || recordDTO.getContext().getOperationType() == MetadataDataMethodOpEnum.UPDATE;
    }

    @Override
    /**
     * 字段编辑权限校验：
     * 1. allDenied：整体拒绝
     * 2. allAllowed：整体允许
     * 3. 其余：根据可编辑字段集合进行严格或过滤处理
     */
    public void check(SemanticRecordDTO recordDTO) {
        FieldPermission fp = recordDTO.getContext().getPermissionContext().getFieldPermission();
        if (fp.isAllDenied()) { throw new PermissionDeniedException(TYPE, "ALL_DENIED", "无权访问任何字段"); }
        if (fp.isAllAllowed()) { return; }
        Map<String, SemanticValueDTO> data = recordDTO.getValue().getData();
        Set<Long> editableIds = fp.getFields().stream()
                                    .filter(FieldPermissionItem::isCanEdit)
                                    .map(FieldPermissionItem::getFieldId)
                                    .collect(Collectors.toSet());
        Map<String, Long> nameToId = new HashMap<>();
        recordDTO.getEntity().getFields()
            .forEach(f -> nameToId.put(f.getFieldName().toLowerCase(), f.getId()));
        if (isStrictMode()) {
            enforceStrictPermission(data, nameToId, editableIds);
        } else {
            filterNonEditableFields(data, nameToId, editableIds);
        }
    }

    /**
     * 是否启用严格模式（抛异常）
     */
    private boolean isStrictMode() { return true; }

    /**
     * 严格模式：存在不可编辑字段时直接抛错
     */
    private void enforceStrictPermission(Map<String, SemanticValueDTO> data, Map<String, Long> nameToId, Set<Long> editableIds) {
        for (String key : data.keySet()) {
            Long fid = nameToId.get(key.toLowerCase());
            if (fid == null || !editableIds.contains(fid)) {
                throw new PermissionDeniedException(TYPE, "FIELD_EDIT", "无权编辑字段:" + key);
            }
        }
    }

    /**
     * 过滤模式：移除不可编辑字段，减少抛错
     */
    private void filterNonEditableFields(Map<String, SemanticValueDTO> data, Map<String, Long> nameToId, Set<Long> editableIds) {
        var it = data.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            Long fid = nameToId.get(e.getKey().toLowerCase());
            if (fid == null || !editableIds.contains(fid)) {
                it.remove();
            }
        }
    }

    @Override
    /**
     * 字段权限校验优先级，晚于数据权限
     */
    public int getOrder() { return 30; }
}
