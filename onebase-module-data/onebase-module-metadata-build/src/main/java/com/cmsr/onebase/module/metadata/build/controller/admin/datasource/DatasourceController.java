package com.cmsr.onebase.module.metadata.build.controller.admin.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.ColumnQueryVO;
import com.cmsr.onebase.module.metadata.build.service.datasource.vo.TableQueryVO;
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

/**
 * 管理后台 - 数据源管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据源管理")
@RestController
@RequestMapping("/metadata/datasource")
@Validated
public class DatasourceController {

    @Resource
    private MetadataDatasourceBuildService datasourceBuildService;

    @PostMapping("/types")
    @Operation(summary = "获取所有支持的数据源类型")
    public CommonResult<List<DatasourceTypeRespVO>> getDatasourceTypes() {
        List<DatasourceTypeRespVO> types = datasourceBuildService.getDatasourceTypes();
        return success(types);
    }

    @PostMapping("/tables")
    @Operation(summary = "根据数据源ID查询表名列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<TableInfoRespVO>> getTablesByDatasourceId(@Valid TableQueryReqVO reqVO) {
        // 将Controller层的VO转换为Service层的VO
        TableQueryVO queryVO = new TableQueryVO(reqVO.getDatasourceId(), reqVO.getSchemaName(), reqVO.getKeyword());
        List<TableInfoRespVO> tables = datasourceBuildService.getTablesByDatasourceId(queryVO);
        return success(tables);
    }

    @PostMapping("/columns")
    @Operation(summary = "根据表名查询字段信息")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<ColumnInfoRespVO>> getColumnsByTableName(@Valid ColumnQueryReqVO reqVO) {
        // 将Controller层的VO转换为Service层的VO
        ColumnQueryVO queryVO = new ColumnQueryVO(reqVO.getDatasourceId(), reqVO.getTableName(), reqVO.getSchemaName());
        List<ColumnInfoRespVO> columns = datasourceBuildService.getColumnsByTableName(queryVO);
        return success(columns);
    }

    @PostMapping("/create")
    @Operation(summary = "新增数据源")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:create')")
    public CommonResult<String> createDatasource(@Valid @RequestBody DatasourceSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        Long id = datasourceBuildService.createDatasource(reqVO);
        return success(id.toString());
    }

    @PostMapping("/update")
    @Operation(summary = "修改数据源")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:update')")
    public CommonResult<Boolean> updateDatasource(@Valid @RequestBody DatasourceSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        datasourceBuildService.updateDatasource(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除数据源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:delete')")
    public CommonResult<Boolean> deleteDatasource(@RequestParam("id") Long id) {
        datasourceBuildService.deleteDatasource(id);
        return success(true);
    }

    @PostMapping("/get")
    @Operation(summary = "获得数据源详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<DatasourceRespVO> getDatasource(@RequestParam("id") Long id) {
        MetadataDatasourceDO datasource = datasourceBuildService.getDatasource(id);
        return success(datasourceBuildService.buildDatasourceRespVO(datasource));
    }

    @PostMapping("/page")
    @Operation(summary = "获得数据源分页列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<PageResult<DatasourceRespVO>> getDatasourcePage(@Valid @RequestBody DatasourcePageReqVO pageReqVO) {
        PageResult<MetadataDatasourceDO> pageResult = datasourceBuildService.getDatasourcePage(pageReqVO);
        PageResult<DatasourceRespVO> convertedResult = new PageResult<>();
        convertedResult.setTotal(pageResult.getTotal());
    convertedResult.setList(datasourceBuildService.buildDatasourceRespVOList(pageResult.getList()));
        return success(convertedResult);
    }

    @PostMapping("/list")
    @Operation(summary = "获得数据源列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<DatasourceRespVO>> getDatasourceList(@Valid @RequestBody DatasourceListReqVO reqVO) {
        List<MetadataDatasourceDO> list;

        // 使用请求头中的应用ID
        Long applicationId = ApplicationManager.getApplicationId();
        if (applicationId != null) {
            list = datasourceBuildService.getDatasourceListByAppId(applicationId);
        } else {
            list = datasourceBuildService.getDatasourceList();
        }

        List<DatasourceRespVO> respList = datasourceBuildService.buildDatasourceRespVOList(list);
        return success(respList);
    }

    @PostMapping("/get-by-code")
    @Operation(summary = "根据编码获得数据源")
    @Parameter(name = "code", description = "数据源编码", required = true, example = "user_db")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<DatasourceRespVO> getDatasourceByCode(@RequestParam("code") String code) {
        MetadataDatasourceDO datasource = datasourceBuildService.getDatasourceByCode(code);
        return success(datasourceBuildService.buildDatasourceRespVO(datasource));
    }

    @PostMapping("/test-connection")
    @Operation(summary = "测试数据源连接")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:test')")
    public CommonResult<DatasourceTestConnectionRespVO> testConnection(@Valid @RequestBody DatasourceTestConnectionReqVO reqVO) {
        return success(datasourceBuildService.testConnection(reqVO));
    }

}
