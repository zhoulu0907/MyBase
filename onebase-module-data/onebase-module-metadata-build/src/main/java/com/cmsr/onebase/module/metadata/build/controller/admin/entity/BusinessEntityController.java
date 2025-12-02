package com.cmsr.onebase.module.metadata.build.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataBusinessEntityBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务实体管理
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Tag(name = "管理后台 - 业务实体管理")
@RestController
@RequestMapping("/metadata/business-entity")
@Validated
@Slf4j
public class BusinessEntityController {

    @Resource
    private MetadataBusinessEntityBuildService businessEntityService;

    @PostMapping("/create")
    @Operation(summary = "创建业务实体")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:create')")
    public CommonResult<BusinessEntityRespVO> createBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        BusinessEntityRespVO result = businessEntityService.createBusinessEntityWithResponse(reqVO);
        return success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新业务实体信息")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:update')")
    public CommonResult<Boolean> updateBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        businessEntityService.updateBusinessEntity(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "软删除业务实体")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:delete')")
    public CommonResult<Boolean> deleteBusinessEntity(@RequestParam("id") Long id) {
        businessEntityService.deleteBusinessEntity(id);
        return success(true);
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取业务实体详细信息")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<BusinessEntityRespVO> getBusinessEntity(@RequestParam("id") Long id) {
        BusinessEntityRespVO result = businessEntityService.getBusinessEntityDetail(id);
        return success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询业务实体列表")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<PageResult<BusinessEntityRespVO>> getBusinessEntityPage(@Valid BusinessEntityPageReqVO pageReqVO) {
        PageResult<BusinessEntityRespVO> result = businessEntityService.getBusinessEntityPageWithResponse(pageReqVO);
        return success(result);
    }

    @PostMapping("/list-by-datasource")
    @Operation(summary = "根据数据源获得业务实体列表")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<BusinessEntityRespVO>> getBusinessEntityListByDatasourceId(@RequestParam("datasourceId") Long datasourceId) {
        List<BusinessEntityRespVO> result = businessEntityService.getBusinessEntityListByDatasourceIdWithRelationType(datasourceId);
        return success(result);
    }

    @PostMapping("/er-diagram")
    @Operation(summary = "根据数据源ID获取ER图数据", description = "获取指定数据源下所有实体信息、字段信息以及实体间的关联关系，用于前端绘制ER图")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<ERDiagramRespVO> getERDiagramByDatasourceId(@RequestParam("datasourceId") Long datasourceId) {
        ERDiagramRespVO result = businessEntityService.getERDiagramByDatasourceId(datasourceId);
        return success(result);
    }

    @PostMapping("/list-by-app")
    @Operation(summary = "根据应用ID获取实体列表", description = "返回实体ID和名称，用于下拉选择等场景")
    @Parameter(name = "appId", description = "应用ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<SimpleEntityRespVO>> getSimpleEntityListByAppId(@RequestParam("appId") Long appId) {
        appId = ApplicationManager.getApplicationId();
        List<SimpleEntityRespVO> result = businessEntityService.getSimpleEntityListByAppId(appId);
        return success(result);
    }

}
