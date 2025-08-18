package com.cmsr.onebase.module.metadata.service.datasource;

import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;

import java.util.List;

/**
 * 应用与数据源关联Service接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataAppAndDatasourceService {

    /**
     * 创建应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @param datasourceType 数据源类型
     * @param appUid 应用UID
     * @return 关联关系ID
     */
    Long createRelation(Long applicationId, Long datasourceId, String datasourceType, String appUid);

    /**
     * 删除应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return 是否删除成功
     */
    boolean deleteRelation(Long applicationId, Long datasourceId);

    /**
     * 根据应用ID获取关联的数据源列表
     *
     * @param applicationId 应用ID
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourcesByApplicationId(Long applicationId);

    /**
     * 根据数据源ID获取关联的应用ID列表
     *
     * @param datasourceId 数据源ID
     * @return 应用ID列表
     */
    List<Long> getApplicationIdsByDatasourceId(Long datasourceId);

    /**
     * 检查应用和数据源是否已关联
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return 是否已关联
     */
    boolean isRelationExists(Long applicationId, Long datasourceId);

    /**
     * 根据应用ID删除所有关联关系
     *
     * @param applicationId 应用ID
     * @return 删除的关联关系数量
     */
    long deleteRelationsByApplicationId(Long applicationId);

    /**
     * 根据数据源ID删除所有关联关系
     *
     * @param datasourceId 数据源ID
     * @return 删除的关联关系数量
     */
    long deleteRelationsByDatasourceId(Long datasourceId);

    /**
     * 根据应用UID获取关联的数据源列表
     *
     * @param appUid 应用UID
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourcesByAppUid(String appUid);

    /**
     * 根据数据源类型获取关联关系列表
     *
     * @param datasourceType 数据源类型
     * @return 关联关系列表
     */
    List<MetadataAppAndDatasourceDO> getRelationsByDatasourceType(String datasourceType);
}
