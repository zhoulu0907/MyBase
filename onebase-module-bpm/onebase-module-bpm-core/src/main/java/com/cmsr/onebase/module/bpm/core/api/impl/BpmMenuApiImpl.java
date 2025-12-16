package com.cmsr.onebase.module.bpm.core.api.impl;

import com.cmsr.onebase.module.bpm.api.menu.BpmMenuApi;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowSkip;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowNodeRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowSkipRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liyang
 * @date 2025-12-16
 */
@Service
public class BpmMenuApiImpl implements BpmMenuApi {
    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Resource
    private FlowNodeRepository flowNodeRepository;

    @Resource
    private FlowSkipRepository flowSkipRepository;

    @Override
    public void removeAppMenu(String menuUuid) {
        if (StringUtils.isBlank(menuUuid)) {
            return;
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(FlowDefinition::getFormPath, menuUuid);

        List<FlowDefinition> flowDefinitionList = flowDefinitionRepository.list(queryWrapper);

        if (CollectionUtils.isEmpty(flowDefinitionList)) {
            return;
        }

        Set<Long> defIds = new HashSet<>();

        for (FlowDefinition flowDefinition : flowDefinitionList) {
            defIds.add(flowDefinition.getId());
        }

        // 删除node
        QueryWrapper nodeQueryWrapper = new QueryWrapper();
        nodeQueryWrapper.in(FlowNode::getDefinitionId, defIds);
        flowNodeRepository.remove(nodeQueryWrapper);

        // 删除skip
        QueryWrapper skipQueryWrapper = new QueryWrapper();
        skipQueryWrapper.in(FlowSkip::getDefinitionId, defIds);
        flowSkipRepository.remove(skipQueryWrapper);

        // 删除definition
        flowDefinitionRepository.remove(queryWrapper);
    }
}
