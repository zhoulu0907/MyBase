package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessFormRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessFormDO;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.data.StartFormNodeData;
import com.cmsr.onebase.module.flow.core.rule.ExpressionAssistant;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    private FieldAssistant fieldAssistant;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private ExpressionAssistant expressionAssistant;

    @Override
    public List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId) {
        List<Long> processIds = flowProcessFormRepository.findByPageId(pageId)
                .stream().map(FlowProcessFormDO::getProcessId).toList();
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findAllByIds(processIds);
        return null;
    }


    @Override
    public FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO) {
        Map<String, Object> inputMap = fieldAssistant.convertInputParamsData(reqVO.getInputParams());
        StartFormNodeData startFormNodeData = graphFlowCache.getStartFormNodeData(reqVO.getProcessId());
        fieldAssistant.fillFilterFieldData(startFormNodeData.getFilterCondition());
        if (startFormNodeData.getCompiledExpression() == null) {
            Serializable compileExpression = expressionAssistant.compileExpression(startFormNodeData.getFilterCondition());
            startFormNodeData.setCompiledExpression(compileExpression);
        }
        boolean isTrigger = expressionAssistant.evaluate(startFormNodeData.getCompiledExpression(), inputMap);
        if (!isTrigger) {
            FormTriggerRespVO respVO = new FormTriggerRespVO();
            respVO.setTriggered(0);

        }
        String chainId = FlowUtils.toFlowChainId(reqVO.getProcessId());
        DefaultContext defaultContext = new DefaultContext();
        defaultContext.setData(FlowUtils.INPUT, inputMap);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, "", defaultContext);
        DefaultContext resultContext = response.getContextBean(DefaultContext.class);
        return BeanUtils.toBean(resultContext.getDataMap(), FormTriggerRespVO.class);
    }
}
