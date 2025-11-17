package com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.ExecTaskStrategy;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 抽象策略基类
 *
 * @author liyang
 * @date 2025-11-03
 */
@Service
public abstract class AbstractExecTaskStrategy<T extends BaseNodeExtDTO> implements ExecTaskStrategy<T> {
    @Resource
    protected TaskService taskService;

    @Resource
    protected HisTaskService hisTaskService;

    @Resource
    protected UserService userService;

    @Resource
    protected DataMethodApi dataMethodApi;

    @Resource
    protected MetadataEntityFieldApi metadataEntityFieldApi;

    @Resource
    protected BpmFlowInsBizExtRepository insBizExtRepository;

    /**
     *  获取实体字段信息的主键ID
     *
     * @param entityId 实体Id
     */
    protected Long getIdFiledId(Long entityId) {
        EntityFieldQueryReqDTO queryReqDTO = new EntityFieldQueryReqDTO();
        queryReqDTO.setEntityId(entityId);

        // 查询实体的所有字段
        List<EntityFieldRespDTO> entityFields = metadataEntityFieldApi.getEntityFieldList(queryReqDTO);

        if (CollectionUtils.isEmpty(entityFields)) {
            throw exception(ErrorCodeConstants.INVALID_ENTITY_ID.getCode(), "获取实体主键字段失败");
        }

        for (EntityFieldRespDTO entityField : entityFields) {
            if (entityField.getFieldType().equals("ID")) {
                return entityField.getId();
            }
        }

        throw exception(ErrorCodeConstants.INVALID_ENTITY_ID.getCode(), "获取实体主键字段失败");
    }

    protected ConditionDTO buildIdCondition(Long entityId, Long entityDataId) {
        // 构建条件
        Long idFiledId = getIdFiledId(entityId);
        ConditionDTO conditionDTO = new ConditionDTO();
        conditionDTO.setFieldId(idFiledId);
        conditionDTO.setOperator(OpEnum.EQUALS.name());
        conditionDTO.setFieldValue(List.of(entityDataId.toString()));

        return conditionDTO;
    }
}


