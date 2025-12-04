package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

/**
 * 数据源管理sdk
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Tag(name = "数据源管理sdk")
public interface MetadataDatasourceApi {

    /**
     * 创建默认数据源
     *
     * @param reqDTO 创建默认数据源请求
     * @return 通用结果
     */
    @Operation(summary = "创建默认数据源")
    Long createDefaultDatasource(@Valid @RequestBody DatasourceCreateDefaultReqDTO reqDTO);

    /**
     * 创建数据源
     *
     * @param reqDTO 创建数据源请求
     * @return 数据源ID
     */
    @Operation(summary = "创建数据源")
    Long createDatasource(@Valid @RequestBody DatasourceSaveReqDTO reqDTO);

    /**
     * 获取数据源信息
     *
     * @param id 数据源ID
     * @return 数据源信息
     */
    @Operation(summary = "获取数据源信息")
    Object getDatasource(@PathVariable("id") Long id);
}
