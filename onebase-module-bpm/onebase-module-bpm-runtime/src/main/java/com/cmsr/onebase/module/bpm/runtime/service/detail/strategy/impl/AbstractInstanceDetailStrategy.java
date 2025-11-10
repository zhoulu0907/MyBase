package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.enums.FieldUiShowModeEnum;
import com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.InstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTaskDetailVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 抽象流程实例详情策略基类
 *
 * @author liyang
 * @date 2025-11-04
 */
public abstract class AbstractInstanceDetailStrategy<T extends BaseNodeExtDTO> implements InstanceDetailStrategy<T> {
    @Resource
    protected MetadataEntityFieldApi metadataEntityFieldApi;

    /**
     * 填充字段只读权限配置（非待办时，所有字段只读）
     *
     * @param vo 详情VO
     * @param entityId 实体ID
     */
    private void fillFieldPermReadOnlyConfig(BpmFlowTaskDetailVO vo, Long entityId) {
        EntityFieldQueryReqDTO queryReqDTO = new EntityFieldQueryReqDTO();
        queryReqDTO.setEntityId(entityId);

        // 查询实体的所有字段
        List<EntityFieldRespDTO> entityFields = metadataEntityFieldApi.getEntityFieldList(queryReqDTO);

        Map<Long, String> fieldPermMap = new HashMap<>();
        Map<Long, String> fieldIdNameMap = new HashMap<>();

        // 默认所有字段都为只读
        for (EntityFieldRespDTO entityField : entityFields) {
            fieldPermMap.put(entityField.getId(), FieldUiShowModeEnum.READ.getCode());
            fieldIdNameMap.put(entityField.getId(), entityField.getFieldName());
        }

        vo.getFormData().put("fieldPerm", fieldPermMap);

        // 这里只是为了方便查看id和name的关联关系，业务上暂时没用上
        vo.getFormData().put("fieldIdName", fieldIdNameMap);
    }

    /**
     * 填充按钮配置（节点类型相关）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     */
    protected void fillButtonConfigs(BpmFlowTaskDetailVO vo, T extDTO) {
        // 由子类实现，默认什么都不做
    }

    /**
     * 填充字段权限配置（节点类型相关）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param entityId 实体ID
     */
    protected void fillFieldPermConfig(BpmFlowTaskDetailVO vo, T extDTO, Long entityId) {
        // 由子类实现，默认什么都不做
    }

    /**
     * 填充详情（只处理节点类型相关的逻辑）
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param task 待办任务（可能为null）
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     */
    @Override
    public void fillDetail(BpmFlowTaskDetailVO vo, T extDTO, Task task, Instance instance, Long loginUserId) {
        Long entityId = (Long) instance.getVariableMap().get("entityId");
        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        // 非当前待办，则没有按钮，且字段权限全部为只读
        if (task == null) {
            // 填充字段权限配置（只读）
            fillFieldPermReadOnlyConfig(vo, entityId);
        } else {
            // 填充按钮信息（节点类型相关）
            fillButtonConfigs(vo, extDTO);

            // 填充字段权限信息（节点类型相关）
            fillFieldPermConfig(vo, extDTO, entityId);

            vo.setTaskId(task.getId());
        }
    }
}

