package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.vo.*;

/**
 * 流程代理接口
 */
public interface BpmAgentService {
    /*
     * 获取流程代理分页
     */
    PageResult<BpmAgentPageResVO> getAgentPage(BpmAgentPageReqVO pageReqVO);
    /*
     * 创建流程代理
     */
    void create(BpmAgentInsertReqVO reqVO);

    /*
     * 撤销流程代理
     */
    void revoke( BpmAgentRevokeReqVO reqVO);

    /*
     * 更新流程代理
     */
    void update(BpmAgentUpdateReqVO reqVO);

}
