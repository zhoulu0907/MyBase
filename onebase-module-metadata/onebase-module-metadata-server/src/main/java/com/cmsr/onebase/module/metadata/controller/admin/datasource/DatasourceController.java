package com.cmsr.onebase.module.metadata.controller.admin.datasource;

import com.cmsr.onebase.framework.apilog.core.annotation.ApiAccessLog;
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

@Tag(name = "管理后台 - 数据源")
@RestController
@RequestMapping("/metadata/datasource")
@Validated
public class DatasourceController {

    @Resource
    private MetadataDatasourceService datasourceService;

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

    // TODO: 实现获取支持的数据源类型功能
    @GetMapping("/types")
    @Operation(summary = "获取支持的数据源类型列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<DatasourceTypeRespVO>> getSupportedDatasourceTypes() {
        // TODO: 调用服务层方法获取支持的数据源类型
        // List<DatasourceTypeRespVO> types = datasourceService.getSupportedDatasourceTypes();
        // return success(types);
        throw new UnsupportedOperationException("TODO: 实现获取支持的数据源类型功能");
    }

    // TODO: 实现数据源连接测试功能
    @PostMapping("/test-connection")
    @Operation(summary = "测试数据源连接")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<DatasourceTestConnectionRespVO> testConnection(@Valid @RequestBody DatasourceTestConnectionReqVO reqVO) {
        // TODO: 调用服务层方法测试连接
        // DatasourceTestConnectionRespVO result = datasourceService.testConnection(reqVO);
        // return success(result);
        throw new UnsupportedOperationException("TODO: 实现数据源连接测试功能");
    }

    // TODO: 实现获取数据源业务实体列表功能
    @GetMapping("/{id}/entities")
    @Operation(summary = "获取数据源的业务实体列表")
    @Parameter(name = "id", description = "数据源编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<BusinessEntityRespVO>> getBusinessEntities(@PathVariable("id") Long id) {
        // TODO: 调用服务层方法获取业务实体
        // List<BusinessEntityRespVO> entities = datasourceService.getBusinessEntities(id);
        // return success(entities);
        throw new UnsupportedOperationException("TODO: 实现获取数据源业务实体列表功能");
    }

    // TODO: 实现获取指定业务实体详情功能
    @GetMapping("/{datasourceId}/entities/{entityName}")
    @Operation(summary = "获取指定业务实体详情")
    @Parameter(name = "datasourceId", description = "数据源编号", required = true, example = "1024")
    @Parameter(name = "entityName", description = "实体名称", required = true, example = "user")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<BusinessEntityRespVO> getBusinessEntity(
            @PathVariable("datasourceId") Long datasourceId,
            @PathVariable("entityName") String entityName) {
        // TODO: 调用服务层方法获取指定业务实体详情
        // BusinessEntityRespVO entity = datasourceService.getBusinessEntity(datasourceId, entityName);
        // return success(entity);
        throw new UnsupportedOperationException("TODO: 实现获取指定业务实体详情功能");
    }


}
