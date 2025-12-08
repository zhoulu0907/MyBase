package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.context.condition.ConditionsSupport;
import com.cmsr.onebase.module.flow.context.enums.FieldTypeConvertor;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.nodes.ModalNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartFormNodeData;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:38
 */
@Slf4j
@Setter
@Service
public class FlowProcessExecServiceImpl implements FlowProcessExecService {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(String pageUuid) {
        Long applicationId = ApplicationManager.getApplicationId();
        List<StartFormNodeData> startFormNodeDataList = FlowProcessCache.findStartFormNodeDataByPageUuid(applicationId, pageUuid);
        return startFormNodeDataList.stream()
                .map(startFormNodeData -> BeanUtils.toBean(startFormNodeData, QueryFormTriggerRespVO.class))
                .toList();
    }

    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        StartFormNodeData startFormNodeData = FlowProcessCache.findStartFormNodeDataByProcessId(reqVO.getProcessId());
        if (startFormNodeData == null) {
            FormTriggerRespVO vo = formNotTriggerRespVO();
            vo.setMessage("流程不存在");
            return vo;
        }
        Map<String, Object> inputMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(reqVO.getInputFields())) {
            inputMap = convertInputFieldsData(reqVO.getInputFields());
        }
        try {
            if (StringUtils.isEmpty(reqVO.getExecutionUuid())) {
                boolean isTrigger = true;
                if (CollectionUtils.isNotEmpty(startFormNodeData.getFilterCondition())) {
                    OrExpression orExpression = ConditionsSupport.convertToOrExpresses(startFormNodeData.getFilterCondition());
                    isTrigger = expressionExecutor.evaluate(orExpression, inputMap);
                }
                if (!isTrigger) {
                    FormTriggerRespVO vo = formNotTriggerRespVO();
                    vo.setMessage("表单不满足触发条件");
                    return vo;
                } else {
                    //TODO 增加记录调用的用户id
                    ExecutorResult executorResult = flowProcessExecutor.execute(FlowUtils.generateTraceId(), reqVO.getProcessId(), inputMap);
                    return formTriggerRespVO(executorResult);
                }
            } else {
                ExecutorResult executorResult = flowProcessExecutor.execute(reqVO.getProcessId(), reqVO.getExecutionUuid(), inputMap);
                return formTriggerRespVO(executorResult);
            }
        } catch (Exception e) {
            log.error("表单触发异常: {}", reqVO, e);
            FormTriggerRespVO vo = formNotTriggerRespVO();
            vo.setMessage("表单触发异常");
            vo.setCause(ExceptionUtils.getMessage(e));
            return vo;
        }
    }

    private Map<String, Object> convertInputFieldsData(List<ModalNodeData.Field> inputFields) {
        Map<String, Object> result = new HashMap<>();
        for (ModalNodeData.Field field : inputFields) {
            SemanticFieldTypeEnum fieldTypeEnum = SemanticFieldTypeEnum.ofCode(field.getFieldType());
            Object value = FieldTypeConvertor.convert(fieldTypeEnum, field.getValue());
            result.put(field.getId(), value);
        }
        return result;
    }

    private FormTriggerRespVO formNotTriggerRespVO() {
        FormTriggerRespVO respVO = new FormTriggerRespVO();
        respVO.setTriggered(false);
        respVO.setExecutionEnd(true);
        return respVO;
    }

    private FormTriggerRespVO formTriggerRespVO(ExecutorResult executorResult) {
        FormTriggerRespVO respVO = new FormTriggerRespVO();
        respVO.setTraceId(executorResult.getTraceId());
        respVO.setTriggered(true);
        respVO.setSuccess(executorResult.isSuccess());
        respVO.setCode(executorResult.getCode());
        respVO.setMessage(executorResult.getMessage());
        respVO.setCause(ExceptionUtils.getRootCauseMessage(executorResult.getCause()));
        respVO.setExecutionEnd(executorResult.isExecutionEnd());
        respVO.setNodeType(executorResult.getExecutionEndNodeType());
        respVO.setExecutionUuid(executorResult.getExecutionUuid());
        respVO.setOutputParams(executorResult.getOutputParams());
        return respVO;
    }

}
