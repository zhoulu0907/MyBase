package com.cmsr.onebase.module.formula.service;

import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowSkipDO;

import java.util.List;

public interface FlowSkipCoreService {
  /**
     * 保存流程跳转信息
     *
     * @param flowSkipDOS 流程跳转信息
     */
    void saveBatch(List<FlowSkipDO> flowSkipDOS);
    /**
     * 根据流程定义id查询流程跳转信息
     *
     * @param definitionId
     * @return
     */
    List<FlowSkipDO>  queryByDefinitionId  (Long definitionId);

}

