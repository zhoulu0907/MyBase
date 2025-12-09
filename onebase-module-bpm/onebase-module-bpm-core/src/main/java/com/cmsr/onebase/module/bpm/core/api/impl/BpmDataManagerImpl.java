package com.cmsr.onebase.module.bpm.core.api.impl;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowSkip;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowNodeRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowSkipRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author liyang
 * @date 2025-12-08
 */
@Service
public class BpmDataManagerImpl implements BpmDataManager {
    @Resource
    private BpmFlowAgentRepository agentRepository;

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Resource
    private FlowNodeRepository flowNodeRepository;

    @Resource
    private FlowSkipRepository flowSkipRepository;

    @Resource
    private UidGenerator uidGenerator;

    private void bpmEngineMoveRuntimeToHistory(Long applicationId, Long versionTag) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(FlowDefinition::getApplicationId, applicationId);
        queryWrapper.eq(FlowDefinition::getVersionTag, versionTag);
        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.list(queryWrapper);

        if (CollectionUtils.isEmpty(flowDefinitionList)) {
            return;
        }

        Set<Long> flowDefinitionIds = new HashSet<>();

        for (FlowDefinition flowDefinition : flowDefinitionList) {
            flowDefinitionIds.add(flowDefinition.getId());
        }

        // 更新流程定义
        FlowDefinition updateFlowDefinition = new FlowDefinition();
        updateFlowDefinition.setVersionTag(versionTag);

        flowDefinitionRepository.update(updateFlowDefinition, queryWrapper);

        // 更新节点
        QueryWrapper nodeQueryWrapper = new QueryWrapper();
        nodeQueryWrapper.in(FlowNode::getDefinitionId, flowDefinitionIds);

        FlowNode updateFlowNode = new FlowNode();
        updateFlowNode.setVersionTag(versionTag);

        flowNodeRepository.update(updateFlowNode, nodeQueryWrapper);

        // 更新跳转数据
        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, flowDefinitionIds);

        FlowSkip updateFlowSkip = new FlowSkip();
        updateFlowSkip.setVersionTag(versionTag);

        flowSkipRepository.update(updateFlowSkip, skipQueryWrapper);
    }

    /**
     * 将编辑态数据复制为运行态数据
     *
     * @param applicationId 应用ID
     */
    private void bpmEngineCopyEditToRuntime(Long applicationId) {
        // 第一步：根据 applicationId + versionTag（BUILD）查出 def 数据
        QueryWrapper defQueryWrapper = new QueryWrapper();
        defQueryWrapper.eq(FlowDefinition::getApplicationId, applicationId);
        defQueryWrapper.eq(FlowDefinition::getVersionTag, VersionTagEnum.BUILD.getValue());
        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.list(defQueryWrapper);

        if (CollectionUtils.isEmpty(flowDefinitionList)) {
            return;
        }

        // 收集旧 def_id，并构建旧 def_id 到新 def_id 的映射
        Set<Long> oldDefIds = new HashSet<>();
        Map<Long, Long> defIdMapping = new HashMap<>();
        for (FlowDefinition def : flowDefinitionList) {
            Long oldDefId = def.getId();
            Long newDefId = uidGenerator.getUID();
            oldDefIds.add(oldDefId);
            defIdMapping.put(oldDefId, newDefId);
        }

        // 第二步：根据 def 查出 node 和 skip 数据
        QueryWrapper nodeQueryWrapper = new QueryWrapper();
        nodeQueryWrapper.in(FlowNode::getDefinitionId, oldDefIds);
        List<FlowNode> flowNodeList = flowNodeRepository.list(nodeQueryWrapper);

        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, oldDefIds);
        List<FlowSkip> flowSkipList = flowSkipRepository.list(skipQueryWrapper);

        // 第三步：更新 ID 和 versionTag，然后保存
        // 更新 def
        for (FlowDefinition def : flowDefinitionList) {
            Long newDefId = defIdMapping.get(def.getId());
            def.setId(newDefId);
            def.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        }

        // 更新 node
        for (FlowNode node : flowNodeList) {
            Long newDefId = defIdMapping.get(node.getDefinitionId());
            node.setId(uidGenerator.getUID());
            node.setDefinitionId(newDefId);
            node.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        }

        // 更新 skip
        for (FlowSkip skip : flowSkipList) {
            Long newDefId = defIdMapping.get(skip.getDefinitionId());
            skip.setId(uidGenerator.getUID());
            skip.setDefinitionId(newDefId);
            skip.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        }

        // 第四步：批量保存
        if (CollectionUtils.isNotEmpty(flowDefinitionList)) {
            flowDefinitionRepository.saveBatch(flowDefinitionList);
        }

        if (CollectionUtils.isNotEmpty(flowNodeList)) {
            flowNodeRepository.saveBatch(flowNodeList);
        }

        if (CollectionUtils.isNotEmpty(flowSkipList)) {
            flowSkipRepository.saveBatch(flowSkipList);
        }
    }

    /**
     * 备份运行态数据为历史版本
     */
    @Override
    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        // 备份代理数据为历史版本
        agentRepository.moveRuntimeToHistory(applicationId, versionTag);

        // 备份流程引擎数据为历史版本
        bpmEngineMoveRuntimeToHistory(applicationId, versionTag);
    }

    /**
     *  编辑态数据变成运行态数据
      */
    @Override
    public void copyEditToRuntime(Long applicationId) {
        // 编辑态数据变成运行态数据
        agentRepository.copyEditToRuntime(applicationId);

        // 流程引擎编辑态数据变成运行态数据
        bpmEngineCopyEditToRuntime(applicationId);
    }
}
