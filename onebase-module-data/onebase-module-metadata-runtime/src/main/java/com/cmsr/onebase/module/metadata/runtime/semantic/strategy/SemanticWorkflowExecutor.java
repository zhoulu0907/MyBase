package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.flow.api.FlowProcessExecApi;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.PROCESS_ERROR_BEFORE_CREATE;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.PROCESS_ERROR_AFTER_CREATE;

@Component
public class SemanticWorkflowExecutor {
    @Resource
    private FlowProcessExecApi flowProcessExecApi;
    @Resource
    private SemanticFieldNameIdMapper fieldNameIdMapper;

    public void preExecute(SemanticRecordDTO recordDTO) {
        MetadataDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        Long entityId = recordDTO.getEntitySchema().getId();
        Map<String, Object> rawData = recordDTO.getEntityValue().getCurrentEntityRawMap();
        var mapped = fieldNameIdMapper.convertNameToId(entityId, rawData);
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(recordDTO.getRecordContext().getTraceId());
        reqDTO.setEntityId(entityId);
        if (op == MetadataDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        else if (op == MetadataDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_UPDATE);
        else if (op == MetadataDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        else return;
        reqDTO.setFieldData(mapped);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if (!respDTO.isTriggered()) { return; }
        if (!respDTO.isSuccess()) { throw exception(PROCESS_ERROR_BEFORE_CREATE, respDTO.getMessage()); }
    }

    public void postExecute(SemanticRecordDTO recordDTO) {
        MetadataDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        Long entityId = recordDTO.getEntitySchema().getId();
        Map<String, Object> rawData = recordDTO.getEntityValue().getCurrentEntityRawMap();
        var mapped = fieldNameIdMapper.convertNameToId(entityId, rawData);
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(recordDTO.getRecordContext().getTraceId());
        reqDTO.setEntityId(entityId);
        if (op == MetadataDataMethodOpEnum.CREATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        else if (op == MetadataDataMethodOpEnum.UPDATE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
        else if (op == MetadataDataMethodOpEnum.DELETE) reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_DELETE);
        else return;
        reqDTO.setFieldData(mapped);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if (!respDTO.isTriggered()) { return; }
        if (!respDTO.isSuccess()) { throw exception(PROCESS_ERROR_AFTER_CREATE, respDTO.getMessage()); }
    }
}
