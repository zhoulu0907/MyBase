package com.cmsr.onebase.module.formula.service;

import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;

import java.util.List;

public interface FlowDefinitionCoreService {
    /**
     * 根据流程定义code列表查询流程定义
     *
     * @param flowCodeList 流程定义code列表
     * @return List<Definition>
     */
    List<FlowDefinitionDO> queryByCodeList(List<String> flowCodeList);
    /**
     * 保存流程定义
     *
     * @param flowDefinitionDO 流程定义
     */
    void save(FlowDefinitionDO flowDefinitionDO);
    /**
     * 根据id更新流程定义
     *
     * @param flowDefinitionDO 流程定义
     */
    void updateById(FlowDefinitionDO flowDefinitionDO);
    /**
     * 根据id查询流程定义
     *
     * @param flowId 流程定义id
     * @return FlowDefinitionDO
     */
    FlowDefinitionDO  queryById(Long flowId);
    /**
     * 根据表单id查询流程定义
     *
     * @param formId 表单id
     * @return List<FlowDefinitionDO>
     */
    List<FlowDefinitionDO>  queryByFormId(Long formId);
}
