package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;


/**
 * 流程版本管理服务接口
 *
 * @author liyang
 * @date 2025-10-25
 */
public interface BpmVersionMgmtService {
    /**
     * 删除流程
     *
     * @param reqVo
     */
    void delete(BpmDeleteReqVo reqVo);

}
