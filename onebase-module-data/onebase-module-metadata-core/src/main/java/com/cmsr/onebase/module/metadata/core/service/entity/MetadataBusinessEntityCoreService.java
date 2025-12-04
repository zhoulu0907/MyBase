package com.cmsr.onebase.module.metadata.core.service.entity;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 业务实体 Service 接口 - 核心数据层接口
 * TODO: Controller层应该使用build模块中的BusinessEntityBuildService，该接口负责VO转换
 *
 * @author matianyu
 * @date 2025-08-20
 */
public interface MetadataBusinessEntityCoreService {

    /**
     * 创建业务实体
     *
     * @param businessEntity 业务实体DO
     * @return 业务实体编号
     */
    Long createBusinessEntity(@Valid MetadataBusinessEntityDO businessEntity);

    /**
     * 更新业务实体
     *
     * @param businessEntity 业务实体DO
     */
    void updateBusinessEntity(@Valid MetadataBusinessEntityDO businessEntity);

    /**
     * 删除业务实体
     *
     * @param id 业务实体编号
     */
    void deleteBusinessEntity(Long id);

    /**
     * 获得业务实体
     *
     * @param id 业务实体编号
     * @return 业务实体DO
     */
    MetadataBusinessEntityDO getBusinessEntity(Long id);

    /**
     * 根据UUID获得业务实体
     *
     * @param entityUuid 业务实体UUID
     * @return 业务实体DO
     */
    MetadataBusinessEntityDO getBusinessEntityByUuid(String entityUuid);

    /**
     * 根据UUID或ID获得业务实体 - 兼容方法
     *
     * @param entityUuidOrId 业务实体UUID或ID字符串
     * @return 业务实体DO
     * @deprecated 请使用 {@link #getBusinessEntityByUuid(String)} 方法
     */
    @Deprecated
    default MetadataBusinessEntityDO getBusinessEntity(String entityUuidOrId) {
        return getBusinessEntityByUuid(entityUuidOrId);
    }

    /**
     * 获得业务实体列表
     *
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> getBusinessEntityList();

    /**
     * 根据编码获得业务实体
     *
     * @param code 编码
     * @return 业务实体
     */
    MetadataBusinessEntityDO getBusinessEntityByCode(String code);

     /**
     * 根据表名获得业务实体
     *
     * @param tableName 表名
     * @return 业务实体
     */
    MetadataBusinessEntityDO getBusinessEntityByTableName(String tableName);


    /**
     * 根据数据源UUID获得业务实体列表
     *
     * @param datasourceUuid 数据源UUID
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid);

    /**
     * 根据条件查询业务实体列表
     *
     * @param queryWrapper 查询条件
     * @return 业务实体列表
     */
    List<MetadataBusinessEntityDO> findAllByConfig(QueryWrapper queryWrapper);

    /**
     * 根据条件统计业务实体数量
     *
     * @param queryWrapper 查询条件
     * @return 业务实体数量
     */
    long countByConfig(QueryWrapper queryWrapper);

    // TODO: 以下方法需要在build模块中实现，涉及VO转换
    /*
    PageResult<BusinessEntityRespVO> getBusinessEntityPageWithResponse(BusinessEntityPageReqVO pageReqVO);
    BusinessEntityRespVO createBusinessEntityWithResponse(@Valid BusinessEntitySaveReqVO reqVO);
    BusinessEntityRespVO getBusinessEntityDetail(Long id);
    List<BusinessEntityRespVO> getBusinessEntityListByDatasourceIdWithRelationType(Long datasourceId);
    ERDiagramRespVO getERDiagramByDatasourceId(Long datasourceId);
    List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId);
    */
}
