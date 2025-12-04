package com.cmsr.onebase.module.metadata.runtime.service.relationship;

import com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo.EntityWithChildrenRespVO;

/**
 * 运行态 - 实体关系服务接口
 *
 * @author matianyu
 * @date 2025-12-04
 */
public interface MetadataEntityRelationshipRuntimeService {

    /**
     * 根据实体ID查询实体名称及其关联的子表信息
     *
     * @param entityId         实体ID
     * @param relationshipType 关系类型筛选（可选）
     * @return 实体及其关联子表信息
     */
    EntityWithChildrenRespVO getEntityWithChildrenById(Long entityId, String relationshipType);

}
