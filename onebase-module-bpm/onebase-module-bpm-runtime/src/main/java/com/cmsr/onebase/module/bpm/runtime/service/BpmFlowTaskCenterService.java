package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.vo.BpmCcTaskPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmDoneTaskPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmMyCreatedPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.*;

import java.util.List;

public interface BpmFlowTaskCenterService {
    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmTodoTaskPageReqVO pageReqVO);

    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmFlowDoneTaskVO> getDonePage(BpmDoneTaskPageReqVO pageReqVO);

    /**
     * 获取流我创建的流程
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmMyCreatedVO> getMyCreatedPage(BpmMyCreatedPageReqVO pageReqVO);


    /**
     * 获取流程节点列表
     *
     * @param bindingViewId 绑定视图Id
     * @return
     */
    List<ListNodesRespVO.NodeVO> listNodes(Long bindingViewId);
    /**
     * 获取流程抄送我的流程
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmCcTaskPageResVO> getCcPage(BpmCcTaskPageReqVO pageReqVO);
}
