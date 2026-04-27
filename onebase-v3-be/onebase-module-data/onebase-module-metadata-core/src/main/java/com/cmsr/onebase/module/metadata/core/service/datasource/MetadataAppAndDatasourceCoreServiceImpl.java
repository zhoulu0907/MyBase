package com.cmsr.onebase.module.metadata.core.service.datasource;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAppAndDatasourceRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用与数据源关联Service实现类
 *
 * @author bty418
 * @date 2025-01-27
 */
@Service
@Slf4j
public class MetadataAppAndDatasourceCoreServiceImpl implements MetadataAppAndDatasourceCoreService {

    @Resource
    private MetadataAppAndDatasourceRepository appAndDatasourceRepository;

    @Resource
    private MetadataDatasourceCoreService datasourceCoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRelation(Long applicationId, String datasourceUuid, String datasourceType, String appUid) {
        log.info("创建应用{}与数据源{}的关联关系", applicationId, datasourceUuid);
        return appAndDatasourceRepository.createRelation(applicationId, datasourceUuid, datasourceType, appUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRelation(Long applicationId, String datasourceUuid) {
        log.info("删除应用{}与数据源{}的关联关系", applicationId, datasourceUuid);
        return appAndDatasourceRepository.deleteRelation(applicationId, datasourceUuid);
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourcesByApplicationId(Long applicationId) {
        log.debug("查询应用{}关联的数据源", applicationId);

        // 获取关联的数据源UUID列表
        List<String> datasourceUuids = appAndDatasourceRepository.getDatasourceUuidsByApplicationId(applicationId);

        if (datasourceUuids.isEmpty()) {
            log.debug("应用{}未关联任何数据源", applicationId);
            return new ArrayList<>();
        }

        // 根据数据源UUID列表查询数据源详情
        List<MetadataDatasourceDO> datasources = new ArrayList<>();
        for (String datasourceUuid : datasourceUuids) {
            try {
                MetadataDatasourceDO datasource = datasourceCoreService.getDatasourceByUuid(datasourceUuid);
                if (datasource != null) {
                    datasources.add(datasource);
                }
            } catch (Exception e) {
                // 如果数据源不存在或查询失败,记录日志并跳过
                log.warn("查询数据源失败,数据源UUID: {},错误: {}", datasourceUuid, e.getMessage());
            }
        }

        log.debug("应用{}关联的数据源数量: {}", applicationId, datasources.size());
        return datasources;
    }

    @Override
    public List<Long> getApplicationIdsByDatasourceUuid(String datasourceUuid) {
        log.debug("查询数据源{}关联的应用", datasourceUuid);
        return appAndDatasourceRepository.getApplicationIdsByDatasourceUuid(datasourceUuid);
    }

    @Override
    public boolean isRelationExists(Long applicationId, String datasourceUuid) {
        return appAndDatasourceRepository.isRelationExists(applicationId, datasourceUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteRelationsByApplicationId(Long applicationId) {
        log.info("删除应用{}的所有关联关系", applicationId);
        return appAndDatasourceRepository.deleteRelationsByApplicationId(applicationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteRelationsByDatasourceUuid(String datasourceUuid) {
        log.info("删除数据源{}的所有关联关系", datasourceUuid);
        return appAndDatasourceRepository.deleteRelationsByDatasourceUuid(datasourceUuid);
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourcesByAppUid(String appUid) {
        log.debug("查询应用UID{}关联的数据源", appUid);

        // 获取关联的数据源UUID列表
        List<String> datasourceUuids = appAndDatasourceRepository.getDatasourceUuidsByAppUid(appUid);

        if (datasourceUuids.isEmpty()) {
            log.debug("应用UID{}未关联任何数据源", appUid);
            return new ArrayList<>();
        }

        // 根据数据源UUID列表查询数据源详情
        List<MetadataDatasourceDO> datasources = new ArrayList<>();
        for (String datasourceUuid : datasourceUuids) {
            MetadataDatasourceDO datasource = datasourceCoreService.getDatasourceByUuid(datasourceUuid);
            if (datasource != null) {
                datasources.add(datasource);
            }
        }

        log.debug("应用UID{}关联的数据源数量: {}", appUid, datasources.size());
        return datasources;
    }

    @Override
    public List<MetadataAppAndDatasourceDO> getRelationsByDatasourceType(String datasourceType) {
        log.debug("查询数据源类型{}的关联关系", datasourceType);
        return appAndDatasourceRepository.getRelationsByDatasourceType(datasourceType);
    }

    @Override
    public String getAppUidByAppIdAndDatasourceUuid(Long applicationId, String datasourceUuid) {
        return appAndDatasourceRepository.getAppUidByAppIdAndDatasourceUuid(applicationId, datasourceUuid);
    }

    @Override
    public MetadataAppAndDatasourceDO getRelation(Long applicationId, String datasourceUuid) {
        log.debug("查询应用{}与数据源{}的关联关系", applicationId, datasourceUuid);
        return appAndDatasourceRepository.getRelation(applicationId, datasourceUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRelationAppUid(Long applicationId, String datasourceUuid, String newAppUid) {
        log.info("更新应用{}与数据源{}关联关系的appUid为{}", applicationId, datasourceUuid, newAppUid);
        return appAndDatasourceRepository.updateRelationAppUid(applicationId, datasourceUuid, newAppUid);
    }
}
