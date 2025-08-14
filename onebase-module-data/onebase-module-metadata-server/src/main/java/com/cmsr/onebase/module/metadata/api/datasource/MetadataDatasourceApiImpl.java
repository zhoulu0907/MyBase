package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源管理 API 实现类
 *
 * @author matianyu
 * @date 2025-08-13
 */
@RestController
@Validated
@Slf4j
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    @Resource
    private MetadataDatasourceService datasourceService;

    @Override
    public CommonResult<String> createDefaultDatasource(Long appId) {
        log.info("RPC 接口 - 创建默认数据源，应用ID: {}", appId);
        
        Long id = datasourceService.createDefaultDatasource(appId);
        
        log.info("RPC 接口 - 完成创建默认数据源，应用ID: {}，数据源ID: {}", appId, id);
        return CommonResult.success(id.toString());
    }

}
