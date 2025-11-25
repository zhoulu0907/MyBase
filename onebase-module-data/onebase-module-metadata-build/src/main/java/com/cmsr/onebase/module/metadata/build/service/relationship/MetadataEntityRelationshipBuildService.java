package com.cmsr.onebase.module.metadata.build.service.relationship;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityWithChildrenRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.AppEntitiesRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 实体关系 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataEntityRelationshipBuildService {

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

    /**
     * 创建主子关系
     * 默认使用主表的id字段和子表的parent_id字段关联
     * 默认关系类型为一对多，级联操作为新增、删除、查询
     *
     * @param createReqVO 创建信息
     * @return 主子关系响应VO
     */
    ParentChildRelationshipRespVO createParentChildRelationship(@Valid ParentChildRelationshipSaveReqVO createReqVO);

    /**
     * 根据实体ID查询实体名称及其关联的子表信息
     *
     * @param entityId 实体ID
     * @param relationshipType 关系类型筛选（ONE_TO_ONE-一对一, ONE_TO_MANY-一对多），为null时查询所有类型
     * @return 实体及其关联子表信息
     */
    EntityWithChildrenRespVO getEntityWithChildrenById(Long entityId, String relationshipType);

    /**
     * 根据应用ID查询所有实体及字段信息
     * 首先根据appId查询datasourceId，如果有多个datasource，默认用第一个
     * 然后再查询相关的实体表和实体字段表
     *
     * @param appId 应用ID
     * @return 应用实体和字段信息
     */
    AppEntitiesRespVO getAppEntitiesWithFields(Long appId);

    /**
     * 根据条件查询实体关系列表
     *
     * @param configStore 查询条件
     * @return 实体关系列表
     */
    List<MetadataEntityRelationshipDO> findAllByConfig(org.anyline.data.param.init.DefaultConfigStore configStore);

    /**
     * 根据字段id删除关联关系 包括字段作为 源字段和目标字段 两种情况
     * @param fieldId
     * @return
     */
    void deleteRelationShipByFieldId(Long fieldId);

    /**
     * 根据源实体ID和目标实体ID查找关系
     *
     * @param sourceEntityId 源实体ID
     * @param targetEntityId 目标实体ID
     * @return 实体关系列表
     */
    List<MetadataEntityRelationshipDO> findBySourceEntityIdAndTargetEntityId(Long sourceEntityId, Long targetEntityId);
}
