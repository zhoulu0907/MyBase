package com.cmsr.onebase.module.metadata.runtime.service.entity;

import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsBatchQueryReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.SimpleEntityRespVO;

import java.util.List;

/**
 * 运行态 - 业务实体服务接口
 *
 * @author matianyu
 * @date 2025-12-15
 */
public interface MetadataBusinessEntityRuntimeService {

    /**
     * 根据应用ID获取简单实体列表
     *
     * @param appId 应用ID
     * @return 简单实体信息列表
     */
    List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId);

    /**
     * 批量查询实体及完整字段信息（包含一级子表）
     *
     * @param reqVO 批量查询请求VO（entityUuids和tableNames二选一）
     * @return 实体及字段信息列表
     */
    List<EntityWithFieldsRespVO> getEntitiesWithFullFields(EntityWithFieldsBatchQueryReqVO reqVO);
}
