package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;



/**
 * 流程设计服务接口
 *
 * @author liyang
 * @date 2025-10-20
 */
public interface BpmDesignService {

    /**
     * 保存流程设计
     *
     * @param flowDesignVO
     * @return
     */
    Long save(BpmDesignVO flowDesignVO);

    /**
     * 查询流程设计
     *
     * @param id
     * @return
     */
    BpmDesignVO queryById(Long id);

    /**
     * 删除流程设计
     *
     * @param reqVo
     */
    void delete(BpmDeleteReqVo reqVo);
}
