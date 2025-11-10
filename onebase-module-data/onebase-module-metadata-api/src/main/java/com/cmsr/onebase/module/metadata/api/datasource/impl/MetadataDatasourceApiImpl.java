package com.cmsr.onebase.module.metadata.api.datasource.impl;

import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.core.config.MetadataConfig;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源管理 API 默认实现
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    
    @Resource
    private MetadataConfig metadataConfig;

    @Override
    @Operation(summary = "创建默认数据源")
    public Long createDefaultDatasource(@Valid @RequestBody DatasourceCreateDefaultReqDTO reqDTO) {
        try {
            // 从配置类中读取默认数据源配置
            Map<String, Object> config = new HashMap<>();
            config.put("host", metadataConfig.getDefaultDatasourceHost());
            config.put("port", metadataConfig.getDefaultDatasourcePort());
            config.put("database", metadataConfig.getDefaultDatasourceDatabase());
            config.put("username", metadataConfig.getDefaultDatasourceUsername());
            config.put("password", metadataConfig.getDefaultDatasourcePassword());
            
            // 将配置转换为JSON字符串
            String configJson = String.format(
                "{\"host\":\"%s\",\"port\":%d,\"database\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}",
                config.get("host"), config.get("port"), config.get("database"), 
                config.get("username"), config.get("password")
            );

            // 调用 core 模块的基础服务，使用MetadataConfig中配置的数据源类型
            Long datasourceId = metadataDatasourceCoreService.createDefaultDatasource(
                    reqDTO.getAppId(),
                    reqDTO.getAppUid(),
                    metadataConfig.getDefaultDatasourceType(),  // 使用配置的数据源类型
                    configJson
            );

            log.info("创建默认数据源成功，数据源ID: {}，应用ID: {}，类型: {}", 
                    datasourceId, reqDTO.getAppId(), metadataConfig.getDefaultDatasourceType());
            return datasourceId;
        } catch (Exception e) {
            log.error("创建默认数据源失败", e);
            throw new RuntimeException("创建默认数据源失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "创建数据源")
    public Long createDatasource(@Valid @RequestBody DatasourceSaveReqDTO reqDTO) {
        try {
            // 将config转换为JSON字符串
            String configJson = reqDTO.getConfig() != null ? reqDTO.getConfig().toString() : "{}";

            // 创建数据源DO
            MetadataDatasourceDO datasource = MetadataDatasourceDO.builder()
                    .datasourceName(reqDTO.getName())
                    .code(reqDTO.getCode())
                    .datasourceType(reqDTO.getDatasourceType())
                    .config(configJson)
                    .description(reqDTO.getRemark())
                    .runMode(1)
                    .datasourceOrigin(1)
                    .build();

            // 调用 core 模块的基础服务创建数据源
            Long datasourceId = metadataDatasourceCoreService.createDatasource(datasource);

            // 创建关联关系
            metadataDatasourceCoreService.createAppDatasourceRelation(
                    reqDTO.getAppId(),
                    datasourceId,
                    reqDTO.getDatasourceType(),
                    reqDTO.getAppUid()
            );

            log.info("创建数据源成功，数据源ID: {}，应用ID: {}", datasourceId, reqDTO.getAppId());
            return datasourceId;
        } catch (Exception e) {
            log.error("创建数据源失败", e);
            throw new RuntimeException("创建数据源失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取数据源信息")
    public Object getDatasource(@PathVariable("id") Long id) {
        try {
            // 调用 core 模块的基础服务
            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(id);
            log.debug("获取数据源信息成功，ID: {}", id);
            return datasource;
        } catch (Exception e) {
            log.error("获取数据源信息失败，ID: {}", id, e);
            throw new RuntimeException("获取数据源信息失败: " + e.getMessage());
        }
    }
}
