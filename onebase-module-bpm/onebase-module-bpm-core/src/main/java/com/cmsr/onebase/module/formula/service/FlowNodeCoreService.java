package com.cmsr.onebase.module.formula.service;

import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;

import java.util.List;

public interface FlowNodeCoreService {
     /**
     * 保存流程节点
     *
     * @param flowNodes
     */
    void saveBatch(List<FlowNodeDO> flowNodes);
    /**
     * 根据流程定义id查询流程节点
     *
     * @param definitionId
     * @return
     */
    List<FlowNodeDO>  queryByDefinitionId  (Long definitionId);

}

