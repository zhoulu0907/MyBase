package com.cmsr.onebase.module.metadata.controller.admin.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.*;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据源管理")
@RestController
@RequestMapping("/metadata/datasource")
@Validated
public class DatasourceController {

    @Resource
    private MetadataDatasourceService datasourceService;

    @GetMapping("/types")
    @Operation(summary = "获取所有支持的数据源类型")
    public CommonResult<List<DatasourceTypeRespVO>> getDatasourceTypes() {
        List<DatasourceTypeRespVO> types = datasourceService.getDatasourceTypes();
        return success(types);
    }

    @GetMapping("/{datasourceId}/tables")
    @Operation(summary = "根据数据源ID查询表名列表")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1001")
    @Parameter(name = "schemaName", description = "数据库模式名", required = false, example = "public")
    @Parameter(name = "keyword", description = "表名搜索关键词", required = false, example = "user")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<TableInfoRespVO>> getTablesByDatasourceId(
            @PathVariable("datasourceId") Long datasourceId,
            @RequestParam(value = "schemaName", required = false) String schemaName,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<TableInfoRespVO> tables = datasourceService.getTablesByDatasourceId(datasourceId, schemaName, keyword);
        return success(tables);
    }

    @GetMapping("/{datasourceId}/tables/{tableName}/columns")
    @Operation(summary = "根据表名查询字段信息")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1001")
    @Parameter(name = "tableName", description = "表名", required = true, example = "users")
    @Parameter(name = "schemaName", description = "数据库模式名", required = false, example = "public")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<ColumnInfoRespVO>> getColumnsByTableName(
            @PathVariable("datasourceId") Long datasourceId,
            @PathVariable("tableName") String tableName,
            @RequestParam(value = "schemaName", required = false) String schemaName) {
        List<ColumnInfoRespVO> columns = datasourceService.getColumnsByTableName(datasourceId, tableName, schemaName);
        return success(columns);
    }

    @PostMapping("/create")
    @Operation(summary = "新增数据源")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:create')")
    public CommonResult<Long> createDatasource(@Valid @RequestBody DatasourceSaveReqVO reqVO) {
        Long id = datasourceService.createDatasource(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改数据源")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:update')")
    public CommonResult<Boolean> updateDatasource(@Valid @RequestBody DatasourceSaveReqVO reqVO) {
        datasourceService.updateDatasource(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:delete')")
    public CommonResult<Boolean> deleteDatasource(@RequestParam("id") Long id) {
        datasourceService.deleteDatasource(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据源详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<DatasourceRespVO> getDatasource(@RequestParam("id") Long id) {
        MetadataDatasourceDO datasource = datasourceService.getDatasource(id);
        return success(DatasourceConvert.INSTANCE.convert(datasource));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据源分页列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<PageResult<DatasourceRespVO>> getDatasourcePage(@Valid DatasourcePageReqVO pageReqVO) {
        PageResult<MetadataDatasourceDO> pageResult = datasourceService.getDatasourcePage(pageReqVO);
        return success(DatasourceConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得数据源列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<DatasourceRespVO>> getDatasourceList() {
        List<MetadataDatasourceDO> list = datasourceService.getDatasourceList();
        return success(DatasourceConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "根据编码获得数据源")
    @Parameter(name = "code", description = "数据源编码", required = true, example = "user_db")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<DatasourceRespVO> getDatasourceByCode(@RequestParam("code") String code) {
        MetadataDatasourceDO datasource = datasourceService.getDatasourceByCode(code);
        return success(DatasourceConvert.INSTANCE.convert(datasource));
    }

    @PostMapping("/test-connection")
    @Operation(summary = "测试数据源连接")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:test')")
    public CommonResult<DatasourceTestConnectionRespVO> testConnection(@Valid @RequestBody DatasourceTestConnectionReqVO reqVO) {
        return success(datasourceService.testConnection(reqVO));
    }

}
