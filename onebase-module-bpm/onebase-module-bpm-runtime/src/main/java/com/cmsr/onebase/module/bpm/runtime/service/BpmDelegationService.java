package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationInsertReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageResVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationUpdateReqVO;

/**
 * 流程代理接口
 */
public interface BpmDelegationService {
    /*
     * 获取流程代理分页
     */
    PageResult<BpmDelegationPageResVO> getDelegationPage(BpmDelegationPageReqVO pageReqVO);
    /*
     * 创建流程代理
     */
    void create(BpmDelegationInsertReqVO reqVO);

    /*
     * 撤销流程代理
     */
    void revoke(Long delegationId);

    /*
     * 更新流程代理
     */
    void update(BpmDelegationUpdateReqVO reqVO);

}
