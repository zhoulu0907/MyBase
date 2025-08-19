package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceRespDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RPC 服务 - 数据源管理
 *
 * @author matianyu
 * @date 2025-08-13
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 数据源管理")
public interface MetadataDatasourceApi {

    String PREFIX = ApiConstants.PREFIX + "/datasource";

    @PostMapping(PREFIX + "/create-default")
    @Operation(summary = "创建默认数据源")
    CommonResult<String> createDefaultDatasource(@RequestBody DatasourceCreateDefaultReqDTO reqDTO);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "新增数据源")
    CommonResult<String> createDatasource(@RequestBody DatasourceSaveReqDTO reqDTO);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "修改数据源")
    CommonResult<Boolean> updateDatasource(@RequestBody DatasourceSaveReqDTO reqDTO);

    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除数据源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<Boolean> deleteDatasource(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/get")
    @Operation(summary = "获得数据源详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<DatasourceRespDTO> getDatasource(@RequestParam("id") Long id);

}
