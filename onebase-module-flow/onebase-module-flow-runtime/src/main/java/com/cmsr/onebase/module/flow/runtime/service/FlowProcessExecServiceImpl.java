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
import com.cmsr.onebase.module.flow.context.table.ColumnType;
import com.cmsr.onebase.module.flow.context.table.RowData;
import com.cmsr.onebase.module.flow.context.table.TableData;
import com.cmsr.onebase.module.flow.context.table.TableFieldSchemas;
import com.cmsr.onebase.module.flow.core.flow.ExecutorInput;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            RowData rowData = convertInputParamsData(reqVO, startFormNodeData);
            boolean isTrigger = true;
            if (CollectionUtils.isNotEmpty(startFormNodeData.getFilterCondition())) {
                OrExpression orExpression = flowConditionsProvider.formatConditionsForExpression(startFormNodeData.getFilterCondition(), rowData);
                isTrigger = expressionExecutor.evaluateInput(orExpression, rowData);
            }
            if (!isTrigger) {
                FormTriggerRespVO vo = formNotTriggerRespVO();
                vo.setMessage("表单不满足触发条件");
                return vo;
            } else {
                ExecutorInput executorInput = buildExecutorInput(reqVO.getProcessId(), rowData);
                ExecutorResult executorResult = flowProcessExecutor.startExecution(executorInput);
                return formTriggerRespVO(executorResult);
            }
        } else {
            // 前端二次触发，用于表单信息收集等节点流程的继续执行
            RowData inputMap = convertInputFieldsData(reqVO.getInputFields());
            ExecutorInput executorInput = buildExecutorInput(reqVO.getProcessId(), inputMap);
            executorInput.setExecutionUuid(reqVO.getExecutionUuid());
            ExecutorResult executorResult = flowProcessExecutor.resumeExecution(executorInput);
            return formTriggerRespVO(executorResult);
        }
    }

    private ExecutorInput buildExecutorInput(Long processId, RowData inputParams) {
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


    private RowData convertInputParamsData(FormTriggerReqVO reqVO, StartFormNodeData startFormNodeData) {
        Map<String, Object> inputParams = reqVO.getInputParams();
        if (MapUtils.isEmpty(inputParams)) {
            return new RowData();
        }

        TableFieldSchemas tableFieldSchemas = startFormNodeData.getTableFieldSchemas();
        String tableName = startFormNodeData.getTableName();

        RowData rowData = new RowData();
        rowData.setTableName(tableName);

        for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if (tableFieldSchemas.isTableName(fieldName)) {
                //子表的数据
                TableData subTableData = convertToTableData(tableName, tableFieldSchemas, fieldValue);
                rowData.addValue(tableName + "." + fieldName, ColumnType.SUBTABLE, subTableData);
            } else {
                //主表的数据
                SemanticFieldTypeEnum fieldTypeEnum = tableFieldSchemas.getFieldTypeEnum(tableName, fieldName);
                Object convertValue = FieldTypeConvertor.convert(fieldTypeEnum, fieldValue);
                rowData.addValue(tableName + "." + fieldName, ColumnType.SIMPLE, convertValue);
            }
        }
        return rowData;
    }

    private TableData convertToTableData(String tableName, TableFieldSchemas tableFieldSchemas, Object value) {
        if (value == null) {
            return new TableData();
        }
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("子表数据必须是List");
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) value;
        TableData tableData = new TableData();
        tableData.setTableName(tableName);
        for (Map<String, Object> objectMap : list) {
            RowData rowData = new RowData();
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                //
                SemanticFieldTypeEnum fieldTypeEnum = tableFieldSchemas.getFieldTypeEnum(tableName, fieldName);
                Object convertValue = FieldTypeConvertor.convert(fieldTypeEnum, fieldValue);
                rowData.addValue(fieldName, ColumnType.SIMPLE, convertValue);
            }
            tableData.addRowData(rowData);
        }
        return tableData;
    }

    private RowData convertInputFieldsData(List<SimpleField> inputFields) {
        if (CollectionUtils.isEmpty(inputFields)) {
            return new RowData();
        }
        RowData result = new RowData();
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
