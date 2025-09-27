package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.module.flow.context.express.ExpressionAssistant;
import com.cmsr.onebase.module.flow.context.express.OrExpresses;
import com.cmsr.onebase.module.flow.context.field.FieldExpressAssistant;
import com.cmsr.onebase.module.flow.context.field.FieldInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:38
 */
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
    private FieldExpressAssistant fieldExpressAssistant;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private ExpressionAssistant expressionAssistant;

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
        List<Long> ids = fieldExpressAssistant.extractFieldIds(startFormNodeData.getFilterCondition());
        Map<Long, FieldInfo> fieldInfoMap = getFieldInfoMap(ids);
        Map<String, Object> inputMap = fieldExpressAssistant.convertInputParamsData(reqVO.getInputParams(), fieldInfoMap);
        OrExpresses orExpresses = fieldExpressAssistant.convertToExpresses(startFormNodeData.getFilterCondition(), fieldInfoMap);
        if (startFormNodeData.getCompiledExpression() == null) {
            Serializable compileExpression = expressionAssistant.compileExpression(orExpresses);
            startFormNodeData.setCompiledExpression(compileExpression);
        }
        boolean isTrigger = expressionAssistant.evaluate(startFormNodeData.getCompiledExpression(), inputMap);
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

    private Map<Long, FieldInfo> getFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);

        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = metadataEntityFieldApi.getFieldJdbcTypes(reqDTO);

        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> {
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setFieldId(info.getFieldId());
                    fieldInfo.setFieldName(info.getFieldName());
                    fieldInfo.setJdbcType(info.getJdbcType());
                    return fieldInfo;
                }));
    }
}
