package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceImportReqDTO;
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

    /**
     * 导出数据源信息
     *
     * @param applicationId 应用ID
     * @param versionTag 版本标签
     * @return 导出的数据源配置信息
     */
    @Operation(summary = "导出数据源信息")
    Object exportDatasource(Long applicationId, Long versionTag);

    /**
     * 导入数据源信息
     *
     * @param newApplicationId 新应用ID
     * @param appUid 应用UID
     * @param tenantId 租户ID
     * @param versionTag 版本标签
     * @param importData 导入的数据源配置信息
     * @param reqDTO 导入请求参数
     */
    @Operation(summary = "导入数据源信息")
    void importDatasource(Long newApplicationId, String appUid, Long tenantId, Long versionTag, Object importData, DatasourceImportReqDTO reqDTO);


}
