package com.cmsr.onebase.module.metadata.core.service.datasource;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;

import java.util.List;

/**
 * 应用与数据源关联Service接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataAppAndDatasourceCoreService {

    /**
     * 创建应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @param datasourceType 数据源类型
     * @param appUid 应用UID
     * @return 关联关系ID
     */
    Long createRelation(Long applicationId, String datasourceUuid, String datasourceType, String appUid);

    /**
     * 删除应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 是否删除成功
     */
    boolean deleteRelation(Long applicationId, String datasourceUuid);

    /**
     * 根据应用ID获取关联的数据源列表
     *
     * @param applicationId 应用ID
     * @return 数据源列表
     */
    List<MetadataDatasourceDO> getDatasourcesByApplicationId(Long applicationId);

    /**
     * 根据数据源UUID获取关联的应用ID列表
     *
     * @param datasourceUuid 数据源UUID
     * @return 应用ID列表
     */
    List<Long> getApplicationIdsByDatasourceUuid(String datasourceUuid);

    /**
     * 检查应用和数据源是否已关联
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 是否已关联
     */
    boolean isRelationExists(Long applicationId, String datasourceUuid);

    /**
     * 根据应用ID删除所有关联关系
     *
     * @param applicationId 应用ID
     * @return 删除的关联关系数量
     */
    long deleteRelationsByApplicationId(Long applicationId);

    /**
     * 根据数据源UUID删除所有关联关系
     *
     * @param datasourceUuid 数据源UUID
     * @return 删除的关联关系数量
     */
    long deleteRelationsByDatasourceUuid(String datasourceUuid);

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

    /**
     * 根据应用ID与数据源UUID获取应用UID
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return appUid，未找到时返回 null
     */
    String getAppUidByAppIdAndDatasourceUuid(Long applicationId, String datasourceUuid);

    /**
     * 根据应用ID与数据源UUID获取关联关系对象
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 关联关系对象，未找到时返回 null
     */
    MetadataAppAndDatasourceDO getRelation(Long applicationId, String datasourceUuid);

    /**
     * 更新应用与数据源关联关系中的appUid
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @param newAppUid 新的应用UID
     * @return 是否更新成功
     */
    boolean updateRelationAppUid(Long applicationId, String datasourceUuid, String newAppUid);
}
