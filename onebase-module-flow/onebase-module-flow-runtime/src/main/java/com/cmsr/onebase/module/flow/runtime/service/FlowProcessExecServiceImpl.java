package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpresses;
import com.cmsr.onebase.module.flow.context.field.FieldExpressProvider;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessFormRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessFormDO;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.data.StartFormNodeData;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.yomahub.liteflow.core.FlowExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private FlowExecutor flowExecutor;

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessFormRepository flowProcessFormRepository;

    @Autowired
    private FieldExpressProvider fieldExpressProvider;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private ExpressionExecutor expressionExecutor;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId) {
        List<Long> processIds = flowProcessFormRepository.findByPageId(pageId)
                .stream().map(FlowProcessFormDO::getProcessId).toList();
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findAllByIds(processIds);
        return null;
    }


    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        StartFormNodeData startFormNodeData = graphFlowCache.getStartFormNodeData(reqVO.getProcessId());
        List<Long> ids = extractFieldIds(startFormNodeData.getFilterCondition());
        Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = getFieldInfoMap(ids);
        Map<String, Object> inputMap = convertInputParamsData(reqVO.getInputParams(), fieldInfoMap);

        OrExpresses orExpresses = Condition.convertToOrExpresses(startFormNodeData.getFilterCondition());
        if (startFormNodeData.getCompiledExpression() == null) {
            JexlExpression compileExpression = expressionExecutor.compileExpression(orExpresses);
            startFormNodeData.setCompiledExpression(compileExpression);
        }
        boolean isTrigger = expressionExecutor.evaluate(startFormNodeData.getCompiledExpression(), inputMap);
        if (!isTrigger) {
            FormTriggerRespVO respVO = new FormTriggerRespVO();
            respVO.setTriggered(0);
            return respVO;
        } else {
            Map<String, Object> outputMap = flowProcessExecutor.execute(reqVO.getProcessId(), inputMap);
            FormTriggerRespVO respVO = new FormTriggerRespVO();
            respVO.setTriggered(1);
            respVO.setResult(outputMap);
            return respVO;
        }
    }

    /**
     * 从条件列表中收集所有字段ID
     */
    private List<Long> extractFieldIds(List<ConditionItem> conditions) {
        return conditions.stream()
                .flatMap(condition -> condition.getRules().stream())
                .map(ruleItem -> NumberUtils.toLong(ruleItem.getFieldId()))
                .distinct()
                .collect(Collectors.toList());
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
            result.put(fieldInfo.getFieldName(), convertedValue);
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
