package com.cmsr.onebase.module.formula.service.impl;

import com.cmsr.onebase.module.formula.dal.database.FlowDefinitionRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;
import com.cmsr.onebase.module.formula.service.FlowDefinitionCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class FlowDefinitionCoreServiceImpl implements FlowDefinitionCoreService {
    @Resource
    private FlowDefinitionRepository   flowDefinitionRepository;
    @Override
    public List<FlowDefinitionDO> queryByCodeList(List<String> flowCodeList) {
        return flowDefinitionRepository.queryByCodeList(flowCodeList);
    }

    @Override
    public void save(FlowDefinitionDO flowDefinitionDO) {
        flowDefinitionRepository.insert(flowDefinitionDO);
    }

    @Override
    public void updateById(FlowDefinitionDO flowDefinitionDO) {
        flowDefinitionRepository.update(flowDefinitionDO);
    }

    @Override
    public FlowDefinitionDO queryById(Long flowId) {
        return flowDefinitionRepository.findById(flowId);
    }

    @Override
    public List<FlowDefinitionDO> queryByFormId(Long formId) {
        return  flowDefinitionRepository.queryByFormId(formId);
    }
}
