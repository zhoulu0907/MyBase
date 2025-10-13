package com.cmsr.onebase.module.formula.service.impl;

import com.cmsr.onebase.module.formula.dal.database.FlowSkipRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowSkipDO;
import com.cmsr.onebase.module.formula.service.FlowSkipCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class FlowSkipCoreServiceImpl implements FlowSkipCoreService {
    @Resource
    FlowSkipRepository flowSkipRepository;
    /**
     * 保存流程跳转关系
     *
     * @param flowSkipDOS
     */
    @Override
    public void saveBatch(List<FlowSkipDO> flowSkipDOS) {
        flowSkipRepository.insertBatch(flowSkipDOS);
    }

    @Override
    public List<FlowSkipDO> queryByDefinitionId(Long definitionId) {
        return  flowSkipRepository.queryByDefinitionId(definitionId);
    }
}
