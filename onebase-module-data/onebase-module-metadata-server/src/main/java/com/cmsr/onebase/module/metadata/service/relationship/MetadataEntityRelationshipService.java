package com.cmsr.onebase.module.metadata.service.relationship;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.RelationshipTypeRespVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 实体关系 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataEntityRelationshipService {

    /**
     * 创建实体关系
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEntityRelationship(@Valid EntityRelationshipSaveReqVO createReqVO);

    /**
     * 更新实体关系
     *
     * @param updateReqVO 更新信息
     */
    void updateEntityRelationship(@Valid EntityRelationshipSaveReqVO updateReqVO);

    /**
     * 删除实体关系
     *
     * @param id 编号
     */
    void deleteEntityRelationship(Long id);

    /**
     * 获取实体关系详情
     *
     * @param id 编号
     * @return 实体关系详情
     */
    EntityRelationshipRespVO getEntityRelationshipDetail(Long id);

    /**
     * 获取实体关系分页
     *
     * @param pageReqVO 分页查询
     * @return 实体关系分页
     */
    PageResult<EntityRelationshipRespVO> getEntityRelationshipPage(EntityRelationshipPageReqVO pageReqVO);

    /**
     * 获取关系类型列表
     *
     * @return 关系类型列表
     */
    List<RelationshipTypeRespVO> getRelationshipTypes();

    /**
     * 获取级联操作类型列表
     *
     * @return 级联操作类型列表
     */
    List<CascadeTypeRespVO> getCascadeTypes();

    /**
     * 根据数据源ID获取该数据源下所有实体的关系
     *
     * @param datasourceId 数据源ID
     * @return 实体关系列表
     */
    List<EntityRelationshipRespVO> getRelationshipsByDatasourceId(Long datasourceId);

} 