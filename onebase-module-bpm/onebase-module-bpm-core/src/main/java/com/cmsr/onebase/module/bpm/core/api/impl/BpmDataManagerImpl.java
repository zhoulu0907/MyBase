package com.cmsr.onebase.module.bpm.core.api.impl;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.bpm.api.datamanager.BpmDataManager;
import com.cmsr.onebase.module.bpm.core.dto.BpmExportDataDto;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowSkip;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowNodeRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowSkipRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author liyang
 * @date 2025-12-08
 */
@Slf4j
@Service
public class BpmDataManagerImpl implements BpmDataManager {
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
        queryWrapper.eq(FlowDefinition::getVersionTag, VersionTagEnum.RUNTIME.getValue());
        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.getMapper().selectListByQuery(queryWrapper);

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

        flowDefinitionRepository.getMapper().updateByQuery(updateFlowDefinition, queryWrapper);

        // 更新节点
        QueryWrapper nodeQueryWrapper = new QueryWrapper();
        nodeQueryWrapper.in(FlowNode::getDefinitionId, flowDefinitionIds);

        FlowNode updateFlowNode = new FlowNode();
        updateFlowNode.setVersionTag(versionTag);

        flowNodeRepository.getMapper().updateByQuery(updateFlowNode, nodeQueryWrapper);

        // 更新跳转数据
        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, flowDefinitionIds);

        FlowSkip updateFlowSkip = new FlowSkip();
        updateFlowSkip.setVersionTag(versionTag);

        flowSkipRepository.getMapper().updateByQuery(updateFlowSkip, skipQueryWrapper);
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
        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.getMapper().selectListByQuery(defQueryWrapper);

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
        List<FlowNode> flowNodeList = flowNodeRepository.getMapper().selectListByQuery(nodeQueryWrapper);

        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, oldDefIds);
        List<FlowSkip> flowSkipList = flowSkipRepository.getMapper().selectListByQuery(skipQueryWrapper);

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
            flowDefinitionRepository.getMapper().insertBatch(flowDefinitionList);
        }

        if (CollectionUtils.isNotEmpty(flowNodeList)) {
            flowNodeRepository.getMapper().insertBatch(flowNodeList);
        }

        if (CollectionUtils.isNotEmpty(flowSkipList)) {
            flowSkipRepository.getMapper().insertBatch(flowSkipList);
        }
    }

    /**
     * 备份运行态数据为历史版本
     */
    @Override
    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        // 备份流程引擎数据为历史版本
        bpmEngineMoveRuntimeToHistory(applicationId, versionTag);
    }

    /**
     *  编辑态数据变成运行态数据
      */
    @Override
    public void copyEditToRuntime(Long applicationId) {
        // 流程引擎编辑态数据变成运行态数据
        bpmEngineCopyEditToRuntime(applicationId);
    }

    @Override
    public void removeApplicationVersion(Long applicationId, Long versionTag) {
        flowDefinitionRepository.deleteApplicationVersionData(applicationId, versionTag);
        flowNodeRepository.deleteApplicationVersionData(applicationId, versionTag);
        flowSkipRepository.deleteApplicationVersionData(applicationId, versionTag);
    }

    @Override
    public void removeApplication(Long applicationId) {
        flowDefinitionRepository.deleteAllApplicationData(applicationId);
        flowNodeRepository.deleteAllApplicationData(applicationId);
        flowSkipRepository.deleteAllApplicationData(applicationId);
    }

    @Override
    public Object exportApplication(Long applicationId, Long versionTag) {
        BpmExportDataDto exportDataDto = new BpmExportDataDto();

        // 第一步：根据 applicationId + versionTag查出 def 数据
        QueryWrapper defQueryWrapper = new QueryWrapper();
        defQueryWrapper.eq(FlowDefinition::getApplicationId, applicationId);
        defQueryWrapper.eq(FlowDefinition::getVersionTag, versionTag);
        defQueryWrapper.limit(1);
        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.getMapper().selectListByQuery(defQueryWrapper);

        if (CollectionUtils.isEmpty(flowDefinitionList)) {
            return exportDataDto;
        }

        // 收集流程Id
        Set<Long> defIds = new HashSet<>();
        for (FlowDefinition def : flowDefinitionList) {
            Long defId = def.getId();
            defIds.add(defId);
        }

        // 第二步：根据 defId 查出 node 和 skip 数据
        QueryWrapper nodeQueryWrapper = new QueryWrapper();
        nodeQueryWrapper.in(FlowNode::getDefinitionId, defIds);
        List<FlowNode> flowNodeList = flowNodeRepository.getMapper().selectListByQuery(nodeQueryWrapper);

        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, defIds);
        List<FlowSkip> flowSkipList = flowSkipRepository.getMapper().selectListByQuery(skipQueryWrapper);

        exportDataDto.setFlowDefinitions(flowDefinitionList);
        exportDataDto.setFlowNodes(flowNodeList);
        exportDataDto.setFlowSkips(flowSkipList);

        return exportDataDto;
    }

    @Override
    public void importApplication(Long newApplicationId, Long tenantId, Long versionTag, Object bpmConfig) {
        if (bpmConfig == null) {
            return;
        }

        // 转成json String
        String bpmConfigJson = JsonUtils.toJsonString(bpmConfig);

        // 转成对象
        BpmExportDataDto exportDataDto = JsonUtils.parseObject(bpmConfigJson, BpmExportDataDto.class);

        if (exportDataDto == null || CollectionUtils.isEmpty(exportDataDto.getFlowDefinitions())) {
            log.warn("流程定义数据为空，跳过导入，newAppId: {}, tenantId: {}, versionTag: {}", newApplicationId, tenantId, versionTag);
            return;
        }

        if (CollectionUtils.isEmpty(exportDataDto.getFlowNodes()) || CollectionUtils.isEmpty(exportDataDto.getFlowNodes())) {
            log.warn("缺少流程节点或边数据，跳过导入，newAppId: {}, tenantId: {}, versionTag: {}", newApplicationId, tenantId, versionTag);
            return;
        }

        // 收集旧 def_id，并构建旧 def_id 到新 def_id 的映射
        Set<Long> oldDefIds = new HashSet<>();
        Map<Long, Long> defIdMapping = new HashMap<>();
        for (FlowDefinition def : exportDataDto.getFlowDefinitions()) {
            Long oldDefId = def.getId();
            Long newDefId = uidGenerator.getUID();
            oldDefIds.add(oldDefId);
            defIdMapping.put(oldDefId, newDefId);
        }

        List<FlowDefinition> flowDefinitionList = exportDataDto.getFlowDefinitions();
        List<FlowNode> flowNodeList = new ArrayList<>();
        List<FlowSkip> flowSkipList = new ArrayList<>();

        // 更新 def
        for (FlowDefinition def : flowDefinitionList) {
            Long newDefId = defIdMapping.get(def.getId());
            def.setId(newDefId);
            def.setApplicationId(newApplicationId);
            def.setTenantIdByListener(tenantId);
            def.setVersionTag(versionTag);

            // 清理公共字段
            def.clean();
        }

        // 更新 node
        for (FlowNode node : exportDataDto.getFlowNodes()) {
            Long newDefId = defIdMapping.get(node.getDefinitionId());

            if (newDefId == null) {
                log.warn("节点未匹配到流程定义信息 {} {} ", node.getId(), node.getNodeName());
                continue;
            }

            node.setId(uidGenerator.getUID());
            node.setDefinitionId(newDefId);
            node.setVersionTag(versionTag);
            node.setApplicationId(newApplicationId);
            node.setTenantIdByListener(tenantId);

            // 清理公共字段
            node.clean();

            flowNodeList.add(node);
        }

        // 更新 skip
        for (FlowSkip skip : exportDataDto.getFlowSkips()) {
            Long newDefId = defIdMapping.get(skip.getDefinitionId());
            if (newDefId == null) {
                log.warn("边未匹配到流程定义信息 {} {} ", skip.getId(), skip.getSkipName());
                continue;
            }

            skip.setId(uidGenerator.getUID());
            skip.setDefinitionId(newDefId);
            skip.setVersionTag(versionTag);
            skip.setApplicationId(newApplicationId);
            skip.setTenantIdByListener(tenantId);

            // 清理公共字段
            skip.clean();

            flowSkipList.add(skip);
        }

        // 第四步：批量保存
        if (CollectionUtils.isNotEmpty(flowDefinitionList)) {
            flowDefinitionRepository.getMapper().insertBatch(flowDefinitionList);
        }

        if (CollectionUtils.isNotEmpty(flowNodeList)) {
            flowNodeRepository.getMapper().insertBatch(flowNodeList);
        }

        if (CollectionUtils.isNotEmpty(flowSkipList)) {
            flowSkipRepository.getMapper().insertBatch(flowSkipList);
        }
    }
}
