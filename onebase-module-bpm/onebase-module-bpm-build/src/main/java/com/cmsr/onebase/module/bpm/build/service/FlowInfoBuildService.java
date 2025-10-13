package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.FlowDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.FlowInfoReqVO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;

import java.util.List;

/**
 * 流程信息接口
 */
public interface FlowInfoBuildService {

    /**
     * 保存流程信息
     *
     * @param reqVO 流程信息请求
     * @return 是否保存成功
     */
    boolean saveFlowInfo(FlowInfoReqVO reqVO);

    /**
     * 根据流程定义code列表查询流程定义
     *
     * @param flowCodeList 流程定义code列表
     * @return List<Definition>
     */
    List<FlowDefinitionDO> queryByCodeList(List<String> flowCodeList);
    /**
     * 根据流程定义id查询流程信息
     *
     * @param flowId 流程定义id
     * @return FlowInfoReqVO
     */
    FlowInfoReqVO queryByFlowId(Long flowId);

   /**
     * 根据表单id查询流程信息
     *
     * @param formId 表单id
     * @return FlowDefinitionVO
     */
   List<FlowDefinitionVO> queryByFormId   (Long formId);
}