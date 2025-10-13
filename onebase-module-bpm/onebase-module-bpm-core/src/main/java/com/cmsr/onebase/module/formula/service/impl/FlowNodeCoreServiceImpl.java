package com.cmsr.onebase.module.formula.service.impl;

import com.cmsr.onebase.module.formula.dal.database.FlowNodeRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;
import com.cmsr.onebase.module.formula.service.FlowNodeCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class FlowNodeCoreServiceImpl implements FlowNodeCoreService {
    @Resource
    FlowNodeRepository flowNodeRepository;
   /**
     * 保存流程节点
     *
     * @param flowNodes
     */
    @Override
    public void saveBatch(List<FlowNodeDO> flowNodes) {
        flowNodeRepository.insertBatch(flowNodes);
    }

    @Override
    public List<FlowNodeDO> queryByDefinitionId(Long definitionId) {
        return flowNodeRepository.queryByDefinitionId(definitionId);
    }
}
