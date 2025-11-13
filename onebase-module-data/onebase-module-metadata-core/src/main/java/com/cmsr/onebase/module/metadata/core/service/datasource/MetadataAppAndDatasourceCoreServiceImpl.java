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
    public Long createRelation(Long applicationId, Long datasourceId, String datasourceType, String appUid) {
        log.info("创建应用{}与数据源{}的关联关系", applicationId, datasourceId);
        return appAndDatasourceRepository.createRelation(applicationId, datasourceId, datasourceType, appUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRelation(Long applicationId, Long datasourceId) {
        log.info("删除应用{}与数据源{}的关联关系", applicationId, datasourceId);
        return appAndDatasourceRepository.deleteRelation(applicationId, datasourceId);
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourcesByApplicationId(Long applicationId) {
        log.debug("查询应用{}关联的数据源", applicationId);

        // 获取关联的数据源ID列表
        List<Long> datasourceIds = appAndDatasourceRepository.getDatasourceIdsByApplicationId(applicationId);

        if (datasourceIds.isEmpty()) {
            log.debug("应用{}未关联任何数据源", applicationId);
            return new ArrayList<>();
        }

        // 根据数据源ID列表查询数据源详情
        List<MetadataDatasourceDO> datasources = new ArrayList<>();
        for (Long datasourceId : datasourceIds) {
            try {
                MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
                if (datasource != null) {
                    datasources.add(datasource);
                }
            } catch (Exception e) {
                // 如果数据源不存在或查询失败,记录日志并跳过
                log.warn("查询数据源失败,数据源ID: {},错误: {}", datasourceId, e.getMessage());
            }
        }

        log.debug("应用{}关联的数据源数量: {}", applicationId, datasources.size());
        return datasources;
    }

    @Override
    public List<Long> getApplicationIdsByDatasourceId(Long datasourceId) {
        log.debug("查询数据源{}关联的应用", datasourceId);
        return appAndDatasourceRepository.getApplicationIdsByDatasourceId(datasourceId);
    }

    @Override
    public boolean isRelationExists(Long applicationId, Long datasourceId) {
        return appAndDatasourceRepository.isRelationExists(applicationId, datasourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteRelationsByApplicationId(Long applicationId) {
        log.info("删除应用{}的所有关联关系", applicationId);
        return appAndDatasourceRepository.deleteRelationsByApplicationId(applicationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteRelationsByDatasourceId(Long datasourceId) {
        log.info("删除数据源{}的所有关联关系", datasourceId);
        return appAndDatasourceRepository.deleteRelationsByDatasourceId(datasourceId);
    }

    @Override
    public List<MetadataDatasourceDO> getDatasourcesByAppUid(String appUid) {
        log.debug("查询应用UID{}关联的数据源", appUid);

        // 获取关联的数据源ID列表
        List<Long> datasourceIds = appAndDatasourceRepository.getDatasourceIdsByAppUid(appUid);

        if (datasourceIds.isEmpty()) {
            log.debug("应用UID{}未关联任何数据源", appUid);
            return new ArrayList<>();
        }

        // 根据数据源ID列表查询数据源详情
        List<MetadataDatasourceDO> datasources = new ArrayList<>();
        for (Long datasourceId : datasourceIds) {
            MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
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
    public String getAppUidByAppIdAndDatasourceId(Long applicationId, Long datasourceId) {
        return appAndDatasourceRepository.getAppUidByAppIdAndDatasourceId(applicationId, datasourceId);
    }

    @Override
    public MetadataAppAndDatasourceDO getRelation(Long applicationId, Long datasourceId) {
        log.debug("查询应用{}与数据源{}的关联关系", applicationId, datasourceId);
        return appAndDatasourceRepository.getRelation(applicationId, datasourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRelationAppUid(Long applicationId, Long datasourceId, String newAppUid) {
        log.info("更新应用{}与数据源{}关联关系的appUid为{}", applicationId, datasourceId, newAppUid);
        return appAndDatasourceRepository.updateRelationAppUid(applicationId, datasourceId, newAppUid);
    }
}
