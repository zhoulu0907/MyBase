package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentInsRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.ExecTaskStrategy;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * 抽象策略基类
 *
 * @author liyang
 * @date 2025-11-03
 */
@Service
public abstract class AbstractExecTaskStrategy<T extends BaseNodeExtDTO> implements ExecTaskStrategy<T> {
    @Resource(name = "bpmTaskService")
    protected TaskService taskService;

    @Resource
    protected BpmFlowInsBizExtRepository insBizExtRepository;

    @Resource(name = "bpmInsService")
    protected InsService insService;

    @Resource
    protected BpmFlowAgentInsRepository agentInsRepository;

    @Resource
    protected SemanticDynamicDataApi semanticDynamicDataApi;

//    /**
//     *  获取实体字段信息的主键ID
//     *
//     * @param entityId 实体Id
//     */
//    protected Long getIdFiledId(Long entityId) {
//        EntityFieldQueryReqDTO queryReqDTO = new EntityFieldQueryReqDTO();
//        queryReqDTO.setEntityId(entityId);
//
//        // 查询实体的所有字段
//        List<EntityFieldRespDTO> entityFields = metadataEntityFieldApi.getEntityFieldList(queryReqDTO);
//
//        if (CollectionUtils.isEmpty(entityFields)) {
//            throw exception(ErrorCodeConstants.INVALID_ENTITY_ID.getCode(), "获取实体主键字段失败");
//        }
//
//        for (EntityFieldRespDTO entityField : entityFields) {
//            if (entityField.getFieldType().equals("ID")) {
//                return entityField.getId();
//            }
//        }
//
//        throw exception(ErrorCodeConstants.INVALID_ENTITY_ID.getCode(), "获取实体主键字段失败");
//    }
//
//    protected ConditionDTO buildIdCondition(Long entityId, Long entityDataId) {
//        // 构建条件
//        Long idFiledId = getIdFiledId(entityId);
//        ConditionDTO conditionDTO = new ConditionDTO();
//        conditionDTO.setFieldId(idFiledId);
//        conditionDTO.setOperator(OpEnum.EQUALS.name());
//        conditionDTO.setFieldValue(List.of(entityDataId.toString()));
//
//        return conditionDTO;
//    }
}


