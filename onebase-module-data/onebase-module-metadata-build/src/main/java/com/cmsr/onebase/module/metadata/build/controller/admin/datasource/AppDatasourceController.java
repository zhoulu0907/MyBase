package com.cmsr.onebase.module.metadata.build.controller.admin.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo.DatasourceRespVO;
import org.modelmapper.ModelMapper;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 应用与数据源关联管理
 *
 * @author bty418
 * @date 2025-01-27
 */
@Tag(name = "管理后台 - 应用与数据源关联管理")
@RestController
@RequestMapping("/metadata/app-datasource")
@Validated
public class AppDatasourceController {

    @Resource
    private MetadataAppAndDatasourceCoreService appAndDatasourceService;
    @Resource
    private MetadataDatasourceCoreService datasourceCoreService;
    @Resource
    private ModelMapper modelMapper;

    @PostMapping("/create-relation")
    @Operation(summary = "创建应用与数据源的关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:create')")
    public CommonResult<String> createRelation(
            @Parameter(name = "applicationId", description = "应用ID", required = false, example = "1")
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
            @RequestParam("datasourceId") Long datasourceId,
            @Parameter(name = "datasourceType", description = "数据源类型", required = true, example = "POSTGRESQL")
            @RequestParam("datasourceType") String datasourceType,
            @Parameter(name = "appUid", description = "应用UID", example = "app-123")
            @RequestParam(value = "appUid", required = false) String appUid) {

        applicationId = ApplicationManager.getApplicationId();
        // 通过datasourceId获取datasourceUuid
        MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
        Long relationId = appAndDatasourceService.createRelation(applicationId, datasource.getDatasourceUuid(), datasourceType, appUid);
        return success(relationId.toString());
    }

    @PostMapping("/delete-relation")
    @Operation(summary = "删除应用与数据源的关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:delete')")
    public CommonResult<Boolean> deleteRelation(
            @Parameter(name = "applicationId", description = "应用ID", required = false, example = "1")
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
            @RequestParam("datasourceId") Long datasourceId) {

        applicationId = ApplicationManager.getApplicationId();
        // 通过datasourceId获取datasourceUuid
        MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
        boolean success = appAndDatasourceService.deleteRelation(applicationId, datasource.getDatasourceUuid());
        return success(success);
    }

    @PostMapping("/list-by-app")
    @Operation(summary = "根据应用ID获取关联的数据源列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<DatasourceRespVO>> getDatasourcesByApplicationId(
            @Parameter(name = "applicationId", description = "应用ID", required = false, example = "1")
            @RequestParam(value = "applicationId", required = false) Long applicationId) {

        applicationId = ApplicationManager.getApplicationId();
        List<MetadataDatasourceDO> datasources = appAndDatasourceService.getDatasourcesByApplicationId(applicationId);
        List<DatasourceRespVO> respList = datasources.stream()
                .map(datasource -> modelMapper.map(datasource, DatasourceRespVO.class))
                .toList();
        return success(respList);
    }

    @PostMapping("/list-apps-by-datasource")
    @Operation(summary = "根据数据源ID获取关联的应用ID列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<Long>> getApplicationIdsByDatasourceId(
            @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
            @RequestParam("datasourceId") Long datasourceId) {

        // 通过datasourceId获取datasourceUuid
        MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
        List<Long> applicationIds = appAndDatasourceService.getApplicationIdsByDatasourceUuid(datasource.getDatasourceUuid());
        return success(applicationIds);
    }

    @PostMapping("/check-relation")
    @Operation(summary = "检查应用和数据源是否已关联")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<Boolean> checkRelation(
            @Parameter(name = "applicationId", description = "应用ID", required = false, example = "1")
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
            @RequestParam("datasourceId") Long datasourceId) {

        applicationId = ApplicationManager.getApplicationId();
        // 通过datasourceId获取datasourceUuid
        MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
        boolean exists = appAndDatasourceService.isRelationExists(applicationId, datasource.getDatasourceUuid());
        return success(exists);
    }

    @PostMapping("/list-by-app-uid")
    @Operation(summary = "根据应用UID获取关联的数据源列表")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:query')")
    public CommonResult<List<DatasourceRespVO>> getDatasourcesByAppUid(
            @Parameter(name = "appUid", description = "应用UID", required = true, example = "app-123")
            @RequestParam("appUid") String appUid) {

        List<MetadataDatasourceDO> datasources = appAndDatasourceService.getDatasourcesByAppUid(appUid);
        List<DatasourceRespVO> respList = datasources.stream()
                .map(datasource -> modelMapper.map(datasource, DatasourceRespVO.class))
                .toList();
        return success(respList);
    }

    @PostMapping("/delete-relations-by-app")
    @Operation(summary = "根据应用ID删除所有关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:delete')")
    public CommonResult<Long> deleteRelationsByApplicationId(
            @Parameter(name = "applicationId", description = "应用ID", required = false, example = "1")
            @RequestParam(value = "applicationId", required = false) Long applicationId) {

        applicationId = ApplicationManager.getApplicationId();
        long deletedCount = appAndDatasourceService.deleteRelationsByApplicationId(applicationId);
        return success(deletedCount);
    }

    @PostMapping("/delete-relations-by-datasource")
    @Operation(summary = "根据数据源ID删除所有关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:datasource:delete')")
    public CommonResult<Long> deleteRelationsByDatasourceId(
            @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
            @RequestParam("datasourceId") Long datasourceId) {

        // 通过datasourceId获取datasourceUuid
        MetadataDatasourceDO datasource = datasourceCoreService.getDatasource(datasourceId);
        long deletedCount = appAndDatasourceService.deleteRelationsByDatasourceUuid(datasource.getDatasourceUuid());
        return success(deletedCount);
    }
}
