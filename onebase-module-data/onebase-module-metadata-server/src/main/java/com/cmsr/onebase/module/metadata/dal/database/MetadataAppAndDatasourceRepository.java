package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用与数据源关联关系仓储类
 * <p>
 * 提供应用与数据源关联关系相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-27
 */
@Repository
@Slf4j
public class MetadataAppAndDatasourceRepository extends DataRepository<MetadataAppAndDatasourceDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataAppAndDatasourceRepository() {
        super(MetadataAppAndDatasourceDO.class);
    }

    /**
     * 根据应用ID获取关联的数据源ID列表
     *
     * @param applicationId 应用ID
     * @return 数据源ID列表
     */
    public List<Long> getDatasourceIdsByApplicationId(Long applicationId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.APPLICATION_ID, applicationId);
        List<MetadataAppAndDatasourceDO> relations = findAllByConfig(configStore);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getDatasourceId)
                .distinct()
                .toList();
    }

    /**
     * 根据数据源ID获取关联的应用ID列表
     *
     * @param datasourceId 数据源ID
     * @return 应用ID列表
     */
    public List<Long> getApplicationIdsByDatasourceId(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.DATASOURCE_ID, datasourceId);
        List<MetadataAppAndDatasourceDO> relations = findAllByConfig(configStore);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getApplicationId)
                .distinct()
                .toList();
    }

    /**
     * 根据应用ID和数据源ID查询关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return 关联关系对象
     */
    public MetadataAppAndDatasourceDO getRelation(Long applicationId, Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.APPLICATION_ID, applicationId);
        configStore.and(MetadataAppAndDatasourceDO.DATASOURCE_ID, datasourceId);
        return findOne(configStore);
    }

    /**
     * 根据应用ID与数据源ID获取 appUid
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return appUid，未找到时返回 null
     */
    public String getAppUidByAppIdAndDatasourceId(Long applicationId, Long datasourceId) {
        MetadataAppAndDatasourceDO relation = getRelation(applicationId, datasourceId);
        return relation != null ? relation.getAppUid() : null;
    }

    /**
     * 检查应用和数据源是否已关联
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return 是否已关联
     */
    public boolean isRelationExists(Long applicationId, Long datasourceId) {
        return getRelation(applicationId, datasourceId) != null;
    }

    /**
     * 创建应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @param datasourceType 数据源类型
     * @param appUid 应用UID
     * @return 关联关系ID
     */
    public Long createRelation(Long applicationId, Long datasourceId, String datasourceType, String appUid) {
        // 检查关联关系是否已存在
        if (isRelationExists(applicationId, datasourceId)) {
            log.warn("应用{}与数据源{}的关联关系已存在", applicationId, datasourceId);
            return getRelation(applicationId, datasourceId).getId();
        }

        MetadataAppAndDatasourceDO relation = MetadataAppAndDatasourceDO.builder()
                .applicationId(applicationId)
                .datasourceId(datasourceId)
                .datasourceType(datasourceType)
                .appUid(appUid)
                .build();

        insert(relation);
        log.info("创建应用{}与数据源{}的关联关系成功，关联ID: {}", applicationId, datasourceId, relation.getId());
        return relation.getId();
    }

    /**
     * 删除应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceId 数据源ID
     * @return 是否删除成功
     */
    public boolean deleteRelation(Long applicationId, Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.APPLICATION_ID, applicationId);
        configStore.and(MetadataAppAndDatasourceDO.DATASOURCE_ID, datasourceId);
        
        long deletedCount = deleteByConfig(configStore);
        log.info("删除应用{}与数据源{}的关联关系，删除数量: {}", applicationId, datasourceId, deletedCount);
        return deletedCount > 0;
    }

    /**
     * 根据应用ID删除所有关联关系
     *
     * @param applicationId 应用ID
     * @return 删除的关联关系数量
     */
    public long deleteRelationsByApplicationId(Long applicationId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.APPLICATION_ID, applicationId);
        
        long deletedCount = deleteByConfig(configStore);
        log.info("删除应用{}的所有关联关系，删除数量: {}", applicationId, deletedCount);
        return deletedCount;
    }

    /**
     * 根据数据源ID删除所有关联关系
     *
     * @param datasourceId 数据源ID
     * @return 删除的关联关系数量
     */
    public long deleteRelationsByDatasourceId(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.DATASOURCE_ID, datasourceId);
        
        long deletedCount = deleteByConfig(configStore);
        log.info("删除数据源{}的所有关联关系，删除数量: {}", datasourceId, deletedCount);
        return deletedCount;
    }

    /**
     * 根据应用UID获取关联的数据源ID列表
     *
     * @param appUid 应用UID
     * @return 数据源ID列表
     */
    public List<Long> getDatasourceIdsByAppUid(String appUid) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.APP_UID, appUid);
        List<MetadataAppAndDatasourceDO> relations = findAllByConfig(configStore);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getDatasourceId)
                .distinct()
                .toList();
    }

    /**
     * 根据数据源类型获取关联关系列表
     *
     * @param datasourceType 数据源类型
     * @return 关联关系列表
     */
    public List<MetadataAppAndDatasourceDO> getRelationsByDatasourceType(String datasourceType) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataAppAndDatasourceDO.DATASOURCE_TYPE, datasourceType);
        return findAllByConfig(configStore);
    }
}
