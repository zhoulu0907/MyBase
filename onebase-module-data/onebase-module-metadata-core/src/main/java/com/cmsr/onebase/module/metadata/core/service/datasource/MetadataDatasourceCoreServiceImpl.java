package com.cmsr.onebase.module.metadata.core.service.datasource;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

/**
 * 数据源核心基础服务实现类 - 提供核心业务逻辑
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataDatasourceCoreServiceImpl implements MetadataDatasourceCoreService {

    @Resource
    private MetadataDatasourceRepository metadataDatasourceRepository;

    @Resource
    private MetadataAppAndDatasourceCoreService appAndDatasourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDatasource(@Valid MetadataDatasourceDO datasource) {
        // 生成 UUID
        if (datasource.getDatasourceUuid() == null || datasource.getDatasourceUuid().isEmpty()) {
            datasource.setDatasourceUuid(UuidUtils.getUuid());
        }

        // 设置创建人和时间
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now();
        datasource.setCreator(currentUserId);
        datasource.setUpdater(currentUserId);
        datasource.setCreateTime(now);
        datasource.setUpdateTime(now);

        // 插入数据源
        metadataDatasourceRepository.save(datasource);

        log.info("创建数据源成功，ID: {}，UUID: {}，创建人: {}，创建时间: {}",
                datasource.getId(), datasource.getDatasourceUuid(), currentUserId, now);
        return datasource.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefaultDatasource(Long appId, String appUid, String datasourceType, String configJson) {
        // 构建默认数据源DO
        MetadataDatasourceDO datasource = new MetadataDatasourceDO();
        datasource.setDatasourceName("默认数据源");
        datasource.setCode("default_" + System.currentTimeMillis());
        datasource.setDatasourceType(datasourceType);
        datasource.setConfig(configJson);
        datasource.setDescription("系统默认数据源");
        datasource.setVersionTag(1L);
        datasource.setDatasourceOrigin(0);
        datasource.setApplicationId(appId);

        // 创建数据源
        Long datasourceId = createDatasource(datasource);

        // 创建关联关系（使用数据源UUID）
        createAppDatasourceRelation(appId, datasource.getDatasourceUuid(), datasourceType, appUid);

        log.info("创建默认数据源成功，数据源ID: {}，应用ID: {}", datasourceId, appId);
        return datasourceId;
    }

    @Override
    public MetadataDatasourceDO getDatasource(Long id) {
        MetadataDatasourceDO datasource = metadataDatasourceRepository.getDatasourceById(id);
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        return datasource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDatasource(@Valid MetadataDatasourceDO datasource) {
        // 校验存在
        validateDatasourceExists(datasource.getId());

        // 设置更新人和时间
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        datasource.setUpdater(currentUserId);
        datasource.setUpdateTime(LocalDateTime.now());

        // 更新数据源
        metadataDatasourceRepository.updateById(datasource);

        log.info("更新数据源成功，ID: {}，更新人: {}", datasource.getId(), currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDatasource(Long id) {
        // 校验存在
        validateDatasourceExists(id);

        // 删除数据源
        metadataDatasourceRepository.removeById(id);

        log.info("删除数据源成功，ID: {}", id);
    }

    @Override
    public MetadataDatasourceDO getDatasourceByCode(String code) {
        return metadataDatasourceRepository.getDatasourceByCode(code);
    }

    @Override
    public MetadataDatasourceDO getDatasourceByUuid(String datasourceUuid) {
        MetadataDatasourceDO datasource = metadataDatasourceRepository.getDatasourceByUuid(datasourceUuid);
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        return datasource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAppDatasourceRelation(Long appId, String datasourceUuid, String datasourceType, String appUid) {
        // 创建应用与数据源的关联关系
        appAndDatasourceService.createRelation(appId, datasourceUuid, datasourceType, appUid);
        log.info("创建应用数据源关联成功，应用ID: {}，数据源UUID: {}", appId, datasourceUuid);
    }

    /**
     * 校验数据源是否存在
     *
     * @param id 数据源ID
     */
    private void validateDatasourceExists(Long id) {
        if (metadataDatasourceRepository.getDatasourceById(id) == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
    }
}
