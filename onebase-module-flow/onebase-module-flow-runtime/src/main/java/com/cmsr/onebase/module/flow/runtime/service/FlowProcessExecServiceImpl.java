package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.ConditionsSupport;
import com.cmsr.onebase.module.flow.context.enums.FieldTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeConvertor;
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
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:38
 */
@Slf4j
@Setter
@Service
public class FlowProcessExecServiceImpl implements FlowProcessExecService {

    @Autowired
    private FlowProcessCache flowProcessCache;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId) {
        List<StartFormNodeData> startFormNodeDataList = flowProcessCache.findStartFormNodeDataByPageId(pageId);
        return startFormNodeDataList.stream()
                .map(startFormNodeData -> BeanUtils.toBean(startFormNodeData, QueryFormTriggerRespVO.class))
                .toList();
    }


    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        StartFormNodeData startFormNodeData = flowProcessCache.findStartFormNodeDataByProcessId(reqVO.getProcessId());
        if (startFormNodeData == null) {
            FormTriggerRespVO vo = formNotTriggerRespVO();
            vo.setMessage("流程不存在");
            return vo;
        }
        Map<String, Object> inputMap = Collections.emptyMap();
        if (MapUtils.isNotEmpty(reqVO.getInputParams())) {
            List<Long> ids = extractFieldIds(startFormNodeData.getFilterCondition(), reqVO.getInputParams());
            Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = getFieldInfoMap(ids);
            inputMap = convertInputParamsData(reqVO.getInputParams(), fieldInfoMap);
        }
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
            FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getByName(field.getFieldType());
            Object value = JdbcTypeConvertor.convert(fieldTypeEnum.getJdbcType().getCode(), field.getValue());
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

    /**
     * 从条件列表中收集所有字段ID
     */
    private List<Long> extractFieldIds(List<Conditions> conditions, Map<Long, String> inputParams) {
        Set<Long> ids1 = conditions.stream()
                .flatMap(condition -> condition.getConditions().stream())
                .map(ruleItem -> NumberUtils.toLong(ruleItem.getFieldId()))
                .collect(Collectors.toSet());
        Set<Long> ids2 = inputParams.keySet();
        Set<Long> ids = Sets.newHashSet();
        ids.addAll(ids1);
        ids.addAll(ids2);
        return Lists.newArrayList(ids);
    }

    /**
     * 转换字段数据
     *
     * @param inputParams
     * @return
     */
    public Map<String, Object> convertInputParamsData(Map<Long, String> inputParams, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (inputParams == null || inputParams.isEmpty()) {
            return new HashMap<>();
        }
        return convertInputParamsToResult(inputParams, fieldInfoMap);
    }

    /**
     * 转换输入参数为结果映射
     */
    private Map<String, Object> convertInputParamsToResult(Map<Long, String> inputParams,
                                                           Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Long, String> entry : inputParams.entrySet()) {
            Long fieldId = entry.getKey();
            String inputValue = entry.getValue();

            EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(fieldId);
            if (fieldInfo == null) {
                throw new IllegalArgumentException("找不到字段ID为 " + fieldId + " 的字段信息");
            }
            Object convertedValue = convertFieldValue(fieldId, fieldInfo, inputValue);
            result.put(String.valueOf(fieldInfo.getFieldId()), convertedValue);
        }
        return result;
    }

    /**
     * 转换字段值
     */
    private Object convertFieldValue(Long fieldId, EntityFieldJdbcTypeRespDTO fieldInfo, String inputValue) {
        if (inputValue == null || fieldInfo.getJdbcType() == null) {
            return inputValue;
        }
        try {
            return JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), inputValue);
        } catch (Exception e) {
            log.warn("字段数据转换失败，字段ID: {}, 字段名: {}, JDBC类型: {}, 输入值: {}, 错误: {}",
                    fieldId, fieldInfo.getFieldName(), fieldInfo.getJdbcType(), inputValue, e.getMessage());
            return inputValue; // 转换失败时保留原始字符串值
        }
    }

    private Map<Long, EntityFieldJdbcTypeRespDTO> getFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);
        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = metadataEntityFieldApi.getFieldJdbcTypes(reqDTO);
        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> info));
    }
}
