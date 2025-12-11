package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataAppAndDatasourceMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用与数据源关联关系仓储类
 * <p>
 * 提供应用与数据源关联关系相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-27
 */
@Repository
@Slf4j
public class MetadataAppAndDatasourceRepository extends BaseBizRepository<MetadataAppAndDatasourceMapper, MetadataAppAndDatasourceDO> {

    /**
     * 根据应用ID获取关联的数据源UUID列表
     *
     * @param applicationId 应用ID
     * @return 数据源UUID列表
     */
    public List<String> getDatasourceUuidsByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getApplicationId, applicationId);
        List<MetadataAppAndDatasourceDO> relations = list(queryWrapper);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getDatasourceUuid)
                .distinct()
                .toList();
    }

    /**
     * 根据数据源UUID获取关联的应用ID列表
     *
     * @param datasourceUuid 数据源UUID
     * @return 应用ID列表
     */
    public List<Long> getApplicationIdsByDatasourceUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getDatasourceUuid, datasourceUuid);
        List<MetadataAppAndDatasourceDO> relations = list(queryWrapper);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getApplicationId)
                .distinct()
                .toList();
    }

    /**
     * 根据应用ID和数据源UUID查询关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 关联关系对象
     */
    public MetadataAppAndDatasourceDO getRelation(Long applicationId, String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getApplicationId, applicationId)
                .eq(MetadataAppAndDatasourceDO::getDatasourceUuid, datasourceUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据应用ID与数据源UUID获取 appUid
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return appUid，未找到时返回 null
     */
    public String getAppUidByAppIdAndDatasourceUuid(Long applicationId, String datasourceUuid) {
        MetadataAppAndDatasourceDO relation = getRelation(applicationId, datasourceUuid);
        return relation != null ? relation.getAppUid() : null;
    }

    /**
     * 检查应用和数据源是否已关联
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 是否已关联
     */
    public boolean isRelationExists(Long applicationId, String datasourceUuid) {
        return getRelation(applicationId, datasourceUuid) != null;
    }

    /**
     * 创建应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @param datasourceType 数据源类型
     * @param appUid 应用UID
     * @return 关联关系ID
     */
    public Long createRelation(Long applicationId, String datasourceUuid, String datasourceType, String appUid) {
        // 检查关联关系是否已存在
        if (isRelationExists(applicationId, datasourceUuid)) {
            log.warn("应用{}与数据源{}的关联关系已存在", applicationId, datasourceUuid);
            return getRelation(applicationId, datasourceUuid).getId();
        }

        MetadataAppAndDatasourceDO relation = new MetadataAppAndDatasourceDO();
        relation.setApplicationId(applicationId);
        relation.setDatasourceUuid(datasourceUuid);
        relation.setDatasourceType(datasourceType);
        relation.setAppUid(appUid);

        save(relation);
        log.info("创建应用{}与数据源{}的关联关系成功，关联ID: {}", applicationId, datasourceUuid, relation.getId());
        return relation.getId();
    }

    /**
     * 删除应用与数据源的关联关系
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @return 是否删除成功
     */
    public boolean deleteRelation(Long applicationId, String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getApplicationId, applicationId)
                .eq(MetadataAppAndDatasourceDO::getDatasourceUuid, datasourceUuid);

        boolean deleted = remove(queryWrapper);
        log.info("删除应用{}与数据源{}的关联关系，结果: {}", applicationId, datasourceUuid, deleted);
        return deleted;
    }

    /**
     * 根据应用ID删除所有关联关系
     *
     * @param applicationId 应用ID
     * @return 删除的关联关系数量
     */
    public long deleteRelationsByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getApplicationId, applicationId);

        long deletedCount = count(queryWrapper);
        remove(queryWrapper);
        log.info("删除应用{}的所有关联关系，删除数量: {}", applicationId, deletedCount);
        return deletedCount;
    }

    /**
     * 根据数据源UUID删除所有关联关系
     *
     * @param datasourceUuid 数据源UUID
     * @return 删除的关联关系数量
     */
    public long deleteRelationsByDatasourceUuid(String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getDatasourceUuid, datasourceUuid);

        long deletedCount = count(queryWrapper);
        remove(queryWrapper);
        log.info("删除数据源{}的所有关联关系，删除数量: {}", datasourceUuid, deletedCount);
        return deletedCount;
    }

    /**
     * 根据应用UID获取关联的数据源UUID列表
     *
     * @param appUid 应用UID
     * @return 数据源UUID列表
     */
    public List<String> getDatasourceUuidsByAppUid(String appUid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getAppUid, appUid);
        List<MetadataAppAndDatasourceDO> relations = list(queryWrapper);
        return relations.stream()
                .map(MetadataAppAndDatasourceDO::getDatasourceUuid)
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
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataAppAndDatasourceDO::getDatasourceType, datasourceType);
        return list(queryWrapper);
    }

    /**
     * 更新应用与数据源关联关系中的appUid
     *
     * @param applicationId 应用ID
     * @param datasourceUuid 数据源UUID
     * @param newAppUid 新的应用UID
     * @return 是否更新成功
     */
    public boolean updateRelationAppUid(Long applicationId, String datasourceUuid, String newAppUid) {
        MetadataAppAndDatasourceDO relation = getRelation(applicationId, datasourceUuid);
        if (relation == null) {
            log.warn("未找到应用{}与数据源{}的关联关系，无法更新appUid", applicationId, datasourceUuid);
            return false;
        }

        relation.setAppUid(newAppUid);
        updateById(relation);
        log.info("成功更新应用{}与数据源{}关联关系的appUid为{}", applicationId, datasourceUuid, newAppUid);
        return true;
    }
}
