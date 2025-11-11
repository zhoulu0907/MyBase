package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmPublishReqVO;


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
     * 查询流程设计
     *
     * @param businessId 业务ID
     * @return
     */
    BpmDesignVO queryByBusinessId(Long businessId);

    /**
     * 发布流程设计
     *
     * @param reqVo
     * @return
     */
    void publish(BpmPublishReqVO reqVo);
}
