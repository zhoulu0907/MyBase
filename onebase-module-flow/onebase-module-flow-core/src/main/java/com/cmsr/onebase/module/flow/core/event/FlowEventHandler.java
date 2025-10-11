package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:31
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowEventHandler {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private GraphFlowCache graphFlowCache;

    public boolean onProcessUpdate(Long processId) {
        log.info("处理流程更新事件：{}", processId);
        FlowProcessDO processDO = flowProcessRepository.findById(processId);
        if (processDO == null) {
            return false;
        }
        JsonGraph jsonGraph = JsonGraphBuilder.build(processDO.getProcessDefinition());
        String flowChain = jsonGraph.toFlowChain();
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
        //
        graphFlowCache.update(processDO.getId(), jsonGraph);
        return true;
    }

    public boolean onProcessDelete(Long processId) {
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
        //
        graphFlowCache.delete(processId);
        return true;
    }

}