package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.context.condition.SimpleField;
import com.cmsr.onebase.module.flow.context.enums.FieldTypeConvertor;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartFormNodeData;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.flow.core.flow.ExecutorInput;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    private FlowProcessCache flowProcessCache = FlowProcessCache.getInstance();

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId) {
        Long applicationId = ApplicationManager.getApplicationId();
        List<StartFormNodeData> startFormNodeDataList = flowProcessCache.findStartFormNodeDataByPageId(applicationId, pageId);
        return startFormNodeDataList.stream()
                .map(startFormNodeData -> BeanUtils.toBean(startFormNodeData, QueryFormTriggerRespVO.class))
                .toList();
    }

    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        log.info("表单触发: {}", reqVO);
        try {
            FormTriggerRespVO respVO = doTriggerForm(reqVO);
            log.info("表单触发结果: {}", respVO);
            return respVO;
        } catch (Exception e) {
            log.error("表单触发异常: {}", reqVO, e);
            FormTriggerRespVO vo = formNotTriggerRespVO();
            vo.setMessage("表单触发异常");
            vo.setCause(ExceptionUtils.getMessage(e));
            return vo;
        }
    }

    private FormTriggerRespVO doTriggerForm(FormTriggerReqVO reqVO) {
        StartFormNodeData startFormNodeData = flowProcessCache.findStartFormNodeDataByProcessId(reqVO.getProcessId());
        if (startFormNodeData == null) {
            FormTriggerRespVO vo = formNotTriggerRespVO();
            vo.setMessage("流程不存在");
            return vo;
        }

        if (StringUtils.isEmpty(reqVO.getExecutionUuid())) {
            // 前端正常的触发逻辑用于表单数据 提交数据前触发
            Map<String, Object> inputMap = convertInputParamsData(reqVO, startFormNodeData);
            boolean isTrigger = true;
            if (CollectionUtils.isNotEmpty(startFormNodeData.getFilterCondition())) {
                OrExpression orExpression = flowConditionsProvider.formatConditionsForExpression(startFormNodeData.getFilterCondition(), inputMap);
                isTrigger = expressionExecutor.evaluateInput(orExpression, inputMap);
            }
            if (!isTrigger) {
                FormTriggerRespVO vo = formNotTriggerRespVO();
                vo.setMessage("表单不满足触发条件");
                return vo;
            } else {
                ExecutorInput executorInput = buildExecutorInput(reqVO.getProcessId(), inputMap);
                ExecutorResult executorResult = flowProcessExecutor.startExecution(executorInput);
                return formTriggerRespVO(executorResult);
            }
        } else {
            // 前端二次触发，用于表单信息收集等节点流程的继续执行
            Map<String, Object> inputMap = convertInputFieldsData(reqVO.getInputFields());
            ExecutorInput executorInput = buildExecutorInput(reqVO.getProcessId(), inputMap);
            executorInput.setExecutionUuid(reqVO.getExecutionUuid());
            ExecutorResult executorResult = flowProcessExecutor.resumeExecution(executorInput);
            return formTriggerRespVO(executorResult);
        }
    }

    private ExecutorInput buildExecutorInput(Long processId, Map<String, Object> inputParams) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long userDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

        ExecutorInput executorInput = new ExecutorInput();
        executorInput.setTraceId(FlowUtils.generateTraceId());
        executorInput.setProcessId(processId);
        executorInput.setInputParams(inputParams);
        executorInput.setTriggerUserId(loginUserId);
        executorInput.setTriggerUserDeptId(userDeptId);

        return executorInput;
    }


    private Map<String, Object> convertInputParamsData(FormTriggerReqVO reqVO, StartFormNodeData startFormNodeData) {
        String tableName = startFormNodeData.getTableName();
        Map<String, Object> inputParams = reqVO.getInputParams();
        Map<String, SemanticFieldSchemaDTO> fieldSchemaMap = startFormNodeData.getFieldSchemaMap();
        if (MapUtils.isEmpty(inputParams)) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            SemanticFieldSchemaDTO fieldSchema = fieldSchemaMap.get(fieldName);
            SemanticFieldTypeEnum fieldTypeEnum;
            if (fieldSchema == null) {
                fieldTypeEnum = SemanticFieldTypeEnum.TEXT;
            } else {
                fieldTypeEnum = fieldSchema.getFieldTypeEnum();
            }
            Object convertValue = FieldTypeConvertor.convert(fieldTypeEnum, value);
            result.put(tableName + "." + fieldName, convertValue);
        }
        return result;
    }

    private Map<String, Object> convertInputFieldsData(List<SimpleField> inputFields) {
        if (CollectionUtils.isEmpty(inputFields)) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        for (SimpleField field : inputFields) {
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
