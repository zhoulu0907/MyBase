package com.cmsr.onebase.module.flow.core.handler;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.mybatisflex.core.tenant.TenantManager;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/11/1 18:27
 */
@Slf4j
@Service
@Conditional(FlowRuntimeCondition.class)
public class FlowCacheHandler {

    @Setter
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Setter
    @Autowired
    private FlowProcessCache flowProcessCache;

    @Setter
    @Autowired
    private FlowGraphBuilder flowGraphBuilder;


    public void initAllProcess() {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() -> flowProcessRepository.findAllByEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus()));
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                onProcessUpdate(flowProcessDO);
                log.info("加载flowProcess流程成功：{}", flowProcessDO.getId());
            } catch (Exception e) {
                log.error("初始化flowProcessDO异常：{}, {}", flowProcessDO, e.getMessage(), e);
            }
        }
    }

    public String onApplicationChange(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() -> flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus()));
        Set<Long> oldProcessIds = flowProcessCache.findProcessByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            oldProcessIds.remove(flowProcessDO.getId());
        }
        for (Long processId : oldProcessIds) {
            onProcessDelete(processId);
        }
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            onProcessUpdate(flowProcessDO);
        }
        return "删除：" + oldProcessIds + "，添加：" + flowProcessDOS.stream().map(FlowProcessDO::getId).toList();
    }

    public String onApplicationDelete(Long applicationId) {
        Set<Long> ids = flowProcessCache.findProcessByApplicationId(applicationId);
        ids.forEach(id -> {
            String chainId = FlowUtils.toFlowChainId(id);
            FlowBus.removeChain(chainId);
            flowProcessCache.deleteByProcessId(id);
        });
        return "删除：" + ids;
    }


    private void onProcessUpdate(FlowProcessDO processDO) {
        log.info("处理流程更新事件：{}", processDO.getId());
        JsonGraph jsonGraph = flowGraphBuilder.build(processDO.getProcessDefinition());
        if (jsonGraph == null) {
            log.error("流程定义错误：{}", processDO);
            return;
        }
        String flowChain = FlowChainBuilder.toFlowChain(jsonGraph);
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
        //
        flowProcessCache.update(processDO.getApplicationId(), processDO.getId(), jsonGraph);
    }

    private void onProcessDelete(Long processId) {
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
        //
        flowProcessCache.deleteByProcessId(processId);
    }


}
