package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageResVO;
/**
 * 流程代理接口
 */
public interface BpmDelegationService {
    /*
     * 获取流程代理分页
     */
    PageResult<BpmDelegationPageResVO> getDelegationPage(BpmDelegationPageReqVO pageReqVO);

}
