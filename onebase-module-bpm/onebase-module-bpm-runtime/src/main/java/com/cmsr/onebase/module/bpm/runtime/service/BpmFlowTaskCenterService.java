package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmMyCreatedVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;

public interface BpmFlowTaskCenterService {
    /**
     * 获取流程待办分页
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmFlowTodoTaskVO> getTodoPage(BpmFlowTodoTaskPageReqVO pageReqVO);

    /**
     * 获取流程已办分页
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmFlowDoneTaskVO> getDonePage(BpmFlowDoneTaskPageReqVO pageReqVO);

    /**
     * 获取流我创建的流程
     *
     * @param pageReqVO
     * @return
     */
    PageResult<BpmMyCreatedVO> getMyCreatedPage(BpmMyCreatedPageReqVO pageReqVO);



}
