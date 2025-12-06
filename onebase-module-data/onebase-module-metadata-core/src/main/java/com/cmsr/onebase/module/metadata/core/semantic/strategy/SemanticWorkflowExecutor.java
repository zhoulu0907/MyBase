package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.flow.api.FlowProcessExecApi;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.PROCESS_ERROR_BEFORE_CREATE;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.PROCESS_ERROR_AFTER_CREATE;

/**
 * 语义工作流执行器
 *
 * <p>
 * 职责：在语义数据的生命周期关键节点触发流程引擎的实体事件，包括主实体与其关联对象（子表/关联表）。
 *
 * 设计要点：
 * - 主实体：在 BEFORE/AFTER 阶段分别触发对应事件，字段数据采用“字段名→字段ID”的映射后上送流程引擎
 * - 关联对象：遍历连接器，按基数（ONE/MANY）逐条触发目标实体的事件，使用连接器的 targetEntityId 进行字段映射
 * - 事件类型：CREATE/UPDATE/DELETE 三类操作分别对应 BEFORE_* / AFTER_* 的触发
 * - 容错策略：若流程未触发（isTriggered=false）则直接返回；触发失败则抛出明确错误码的异常
 * </p>
 */
@Component
public class SemanticWorkflowExecutor {
    @Resource
    private FlowProcessExecApi flowProcessExecApi;

    /**
     * 前置触发：在数据进入执行阶段之前触发工作流（主实体 + 关联对象）
     *
     * - 主实体：映射字段ID后触发 BEFORE_* 事件
     * - 关联对象：对每个连接器，优先尝试单行（ONE），否则遍历多行（MANY）逐条触发
     */
    public void preExecute(SemanticRecordDTO recordDTO) {
        SemanticDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        String entityId = recordDTO.getEntitySchema().getEntityUuid();
        Map<String, Object> rawData = recordDTO.getEntityValue().getGlobalRawMapForJson();
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(recordDTO.getRecordContext().getTraceId());
        reqDTO.setEntityUuId(entityId);
        reqDTO.setEntityTableName(recordDTO.getEntitySchema().getTableName());
        if (op == SemanticDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        else if (op == SemanticDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_UPDATE);
        else if (op == SemanticDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        else return;
        reqDTO.setColFieldData(rawData);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if (!respDTO.isTriggered()) { return; }
        if (!respDTO.isSuccess()) { throw exception(PROCESS_ERROR_BEFORE_CREATE, respDTO.getMessage()); }

        // 遍历关联对象，按 ONE/MANY 逐条触发目标实体的前置事件
        List<SemanticRelationSchemaDTO> connectors = recordDTO.getEntitySchema().getConnectors();
        if (connectors != null) {
            for (SemanticRelationSchemaDTO c : connectors) {
                if (c == null || c.getTargetEntityUuid() == null) { continue; }
                String name = c.getTargetEntityTableName();
                if (name == null) { continue; }
                Map<String, Object> obj = recordDTO.getEntityValue().getConnectorRawObject(name);
                if (obj != null && !obj.isEmpty()) {
                    triggerConnector(recordDTO, c.getTargetEntityUuid(), op, obj, true);
                } else {
                    List<Map<String, Object>> list = recordDTO.getEntityValue().getConnectorRawList(name);
                    if (list != null) {
                        for (Map<String, Object> row : list) {
                            if (row != null && !row.isEmpty()) { triggerConnector(recordDTO, c.getTargetEntityUuid(), op, row, true); }
                        }
                    }
                }
            }
        }
    }

    /**
     * 后置触发：在数据执行完成之后触发工作流（主实体 + 关联对象）
     *
     * - 主实体：映射字段ID后触发 AFTER_* 事件
     * - 关联对象：同前置流程，按连接器逐条触发
     */
    public void postExecute(SemanticRecordDTO recordDTO) {
        SemanticDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        Map<String, Object> rawData = recordDTO.getEntityValue().getGlobalRawMapForJson();
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(recordDTO.getRecordContext().getTraceId());
        reqDTO.setEntityUuId(recordDTO.getEntitySchema().getEntityUuid());
        reqDTO.setEntityTableName(recordDTO.getEntitySchema().getTableName());
        if (op == SemanticDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        else if (op == SemanticDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
        else if (op == SemanticDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_DELETE);
        else return;
        reqDTO.setColFieldData(rawData);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if (!respDTO.isTriggered()) { return; }
        if (!respDTO.isSuccess()) { throw exception(PROCESS_ERROR_AFTER_CREATE, respDTO.getMessage()); }

        // 遍历关联对象，按 ONE/MANY 逐条触发目标实体的后置事件
        List<SemanticRelationSchemaDTO> connectors = recordDTO.getEntitySchema().getConnectors();
        if (connectors != null) {
            for (SemanticRelationSchemaDTO c : connectors) {
                if (c == null || c.getTargetEntityUuid() == null) { continue; }
                String name = c.getTargetEntityTableName();
                if (name == null) { continue; }
                Map<String, Object> obj = recordDTO.getEntityValue().getConnectorRawObject(name);
                if (obj != null && !obj.isEmpty()) {
                    triggerConnector(recordDTO, c.getTargetEntityUuid(), op, obj, false);
                } else {
                    List<Map<String, Object>> list = recordDTO.getEntityValue().getConnectorRawList(name);
                    if (list != null) {
                        for (Map<String, Object> row : list) {
                            if (row != null && !row.isEmpty()) { triggerConnector(recordDTO, c.getTargetEntityUuid(), op, row, false); }
                        }
                    }
                }
            }
        }
    }

    /**
     * 触发连接器对应目标实体的工作流事件
     *
     * @param recordDTO 语义记录上下文（用于traceId与事件类型判断）
     * @param entityId 目标实体ID（连接器的 targetEntityId）
     * @param op 操作类型（CREATE/UPDATE/DELETE）
     * @param rawData 字段名→原始值的映射（单行数据）
     * @param before 是否为前置事件（true：BEFORE_*；false：AFTER_*）
     */
    private void triggerConnector(SemanticRecordDTO recordDTO, String entityUuid, SemanticDataMethodOpEnum op, Map<String, Object> rawData, boolean before) {
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(recordDTO.getRecordContext().getTraceId());
        reqDTO.setEntityUuId(recordDTO.getEntitySchema().getEntityUuid());
        reqDTO.setEntityTableName(recordDTO.getEntitySchema().getTableName());
        if (before) {
            if (op == SemanticDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
            else if (op == SemanticDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_UPDATE);
            else if (op == SemanticDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
            else return;
        } else {
            if (op == SemanticDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
            else if (op == SemanticDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
            else if (op == SemanticDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_DELETE);
            else return;
        }
        reqDTO.setColFieldData(rawData);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if (!respDTO.isTriggered()) { return; }
        if (!respDTO.isSuccess()) {
            if (before) { throw exception(PROCESS_ERROR_BEFORE_CREATE, respDTO.getMessage()); }
            else { throw exception(PROCESS_ERROR_AFTER_CREATE, respDTO.getMessage()); }
        }
    }
}
