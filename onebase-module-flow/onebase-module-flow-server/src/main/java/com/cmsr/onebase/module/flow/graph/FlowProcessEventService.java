package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.utils.FlowUtils;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:31
 */
@Setter
@Slf4j
public abstract class FlowProcessEventService {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    public abstract void publishProcessAdd(Long processId);

    public abstract void publishProcessDelete(Long processId);

    public abstract void publishProcessUpdate(Long processId);

    public void onProcessAdd(Long processId){
        log.info("发布流程添加事件：{}", processId);
        FlowProcessDO processDO = flowProcessRepository.findById(processId);
        if (processDO == null) {
            return;
        }
        JsonGraph jsonGraph = JsonGraph.of(processDO.getProcessDefinition());
        String flowChain = jsonGraph.toFlowChain();
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
    }

    public void onProcessDelete(Long processId){
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
    }

    public void onProcessUpdate(Long processId){
        log.info("发布流程更新事件：{}", processId);
        FlowProcessDO processDO = flowProcessRepository.findById(processId);
        if (processDO == null) {
            return;
        }
        JsonGraph jsonGraph = JsonGraph.of(processDO.getProcessDefinition());
        String flowChain = jsonGraph.toFlowChain();
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
    }
}