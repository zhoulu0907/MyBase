package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.FieldPermTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.FieldUiShowModeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 审批节点流程实例详情策略
 *
 * @author liyang
 * @date 2025-11-04
 */
@Component
public class ApproverInstanceDetailStrategy extends AbstractInstanceDetailStrategy<ApproverNodeExtDTO> {

    @Override
    public boolean supports(String bizNodeType) {
        return BpmNodeTypeEnum.APPROVER.getCode().equals(bizNodeType);
    }

    @Override
    protected void fillFieldPermConfig(BpmTaskDetailRespVO vo, ApproverNodeExtDTO extDTO, Long entityId, boolean isTodo) {
        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        // 审批节点未配置字段权限，或未开启节点配置，则返回，使用表单默认权限
        if (fieldPermConfig == null || !fieldPermConfig.getUseNodeConfig()) {
            return;
        }

        EntityFieldQueryReqDTO queryReqDTO = new EntityFieldQueryReqDTO();
        queryReqDTO.setEntityId(entityId);

        // 查询实体的所有字段
        List<EntityFieldRespDTO> entityFields = metadataEntityFieldApi.getEntityFieldList(queryReqDTO);

        Map<Long, EntityFieldRespDTO> entityFieldMap = new HashMap<>();
        Map<Long, String> fieldPermMap = new HashMap<>();
        Map<Long, String> fieldIdNameMap = new HashMap<>();

        // 默认所有字段都为只读
        for (EntityFieldRespDTO entityField : entityFields) {
            entityFieldMap.put(entityField.getId(), entityField);
            fieldPermMap.put(entityField.getId(), FieldUiShowModeEnum.READ.getCode());
            fieldIdNameMap.put(entityField.getId(), entityField.getFieldName());
        }

        vo.getFormData().put("fieldPerm", fieldPermMap);

        // 这里只是为了方便查看id和name的关联关系，业务上暂时没用上
        vo.getFormData().put("fieldIdName", fieldIdNameMap);

        // 没有配置字段权限，则返回只读权限
        if (!CollectionUtils.isNotEmpty(fieldPermConfig.getFieldConfigs())) {
            return;
        }

        Set<String> hiddenFieldNames = new HashSet<>();

        // 处理节点配置的字段权限 todo: 处理子表字段权限
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            // 在实体字段中不存在的，直接跳过
            EntityFieldRespDTO entityField = entityFieldMap.get(fieldConfig.getFieldId());
            if (entityField == null) {
                continue;
            }

            // 处理隐藏字段
            if (Objects.equals(FieldPermTypeEnum.HIDDEN.getCode(), fieldConfig.getFieldPermType())) {
                // 这里才是字段的英文名
                hiddenFieldNames.add(entityField.getFieldName());
                fieldPermMap.put(fieldConfig.getFieldId(), FieldUiShowModeEnum.HIDDEN.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.READ.getCode(), fieldConfig.getFieldPermType())) {
                fieldPermMap.put(fieldConfig.getFieldId(), FieldUiShowModeEnum.READ.getCode());
            } else if (Objects.equals(FieldPermTypeEnum.WRITE.getCode(), fieldConfig.getFieldPermType())) {
                if (isTodo) {
                    fieldPermMap.put(fieldConfig.getFieldId(), FieldUiShowModeEnum.WRITE.getCode());
                } else {
                    // 非待办的情况下，都是只读
                    fieldPermMap.put(fieldConfig.getFieldId(), FieldUiShowModeEnum.READ.getCode());
                }
            }
        }

        // 移除隐藏字段在表单数据中的值
        Object entityData = vo.getFormData().get("data");

        if (CollectionUtils.isNotEmpty(hiddenFieldNames) && entityData instanceof Map<?, ?> entityDataMap) {
            for (String hiddenFieldName : hiddenFieldNames) {
                entityDataMap.remove(hiddenFieldName);
            }
        }
    }

    @Override
    protected void fillButtonConfigs(BpmTaskDetailRespVO vo, ApproverNodeExtDTO extDTO) {
        for (ApproverNodeBtnCfgDTO buttonConfig : extDTO.getButtonConfigs()) {
            if (!buttonConfig.getEnabled()) {
               continue;
            }

            if (vo.getButtonConfigs() == null) {
                vo.setButtonConfigs(new ArrayList<>());
            }

            vo.getButtonConfigs().add(buttonConfig);
        }
    }
}

