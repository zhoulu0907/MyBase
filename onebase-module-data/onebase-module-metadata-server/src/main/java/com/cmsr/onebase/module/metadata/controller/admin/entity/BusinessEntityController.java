package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.convert.entity.BusinessEntityConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
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

@Tag(name = "管理后台 - 业务实体")
@RestController
@RequestMapping("/metadata/business-entity")
@Validated
public class BusinessEntityController {

    @Resource
    private MetadataBusinessEntityService businessEntityService;

    @PostMapping("/create")
    @Operation(summary = "新增业务实体")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:create')")
    public CommonResult<Long> createBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        Long id = businessEntityService.createBusinessEntity(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改业务实体")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:update')")
    public CommonResult<Boolean> updateBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        businessEntityService.updateBusinessEntity(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务实体")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:delete')")
    public CommonResult<Boolean> deleteBusinessEntity(@RequestParam("id") Long id) {
        businessEntityService.deleteBusinessEntity(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得业务实体详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<BusinessEntityRespVO> getBusinessEntity(@RequestParam("id") Long id) {
        MetadataBusinessEntityDO businessEntity = businessEntityService.getBusinessEntity(id);
        return success(BusinessEntityConvert.INSTANCE.convert(businessEntity));
    }

    @GetMapping("/page")
    @Operation(summary = "获得业务实体分页列表")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<PageResult<BusinessEntityRespVO>> getBusinessEntityPage(@Valid BusinessEntityPageReqVO pageReqVO) {
        PageResult<MetadataBusinessEntityDO> pageResult = businessEntityService.getBusinessEntityPage(pageReqVO);
        return success(BusinessEntityConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得业务实体列表")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<BusinessEntityRespVO>> getBusinessEntityList() {
        List<MetadataBusinessEntityDO> list = businessEntityService.getBusinessEntityList();
        return success(BusinessEntityConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-by-datasource")
    @Operation(summary = "根据数据源获得业务实体列表")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<BusinessEntityRespVO>> getBusinessEntityListByDatasourceId(@RequestParam("datasourceId") Long datasourceId) {
        List<MetadataBusinessEntityDO> list = businessEntityService.getBusinessEntityListByDatasourceId(datasourceId);
        return success(BusinessEntityConvert.INSTANCE.convertList(list));
    }

}
