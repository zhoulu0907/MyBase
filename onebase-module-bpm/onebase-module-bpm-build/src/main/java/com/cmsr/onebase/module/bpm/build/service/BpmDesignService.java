package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignRespVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignSaveReqVO;
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
    Long save(BpmDesignSaveReqVO flowDesignVO);

    /**
     * 查询流程设计
     *
     * @param id
     * @return
     */
    BpmDesignRespVO queryById(Long id);

    /**
     * 查询流程设计
     *
     * @param businessId 菜单UUID
     * @return
     */
    BpmDesignRespVO queryByBusinessId(Long businessId);

    /**
     * 查询流程设计
     *
     * @param businessUuid 菜单UUID
     * @return
     */
    BpmDesignRespVO queryByBusinessUuid(String businessUuid);

    /**
     * 发布流程设计
     *
     * @param reqVo
     * @return
     */
    void publish(BpmPublishReqVO reqVo);
}
