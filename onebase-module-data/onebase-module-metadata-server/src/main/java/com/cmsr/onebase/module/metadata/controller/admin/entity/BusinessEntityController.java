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

@Tag(name = "管理后台 - 业务实体管理")
@RestController
@RequestMapping("/metadata/business-entity")
@Validated
public class BusinessEntityController {

    @Resource
    private MetadataBusinessEntityService businessEntityService;

    @PostMapping
    @Operation(summary = "创建业务实体")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:create')")
    public CommonResult<BusinessEntityRespVO> createBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        Long id = businessEntityService.createBusinessEntity(reqVO);
        MetadataBusinessEntityDO businessEntity = businessEntityService.getBusinessEntity(id);
        return success(BusinessEntityConvert.INSTANCE.convert(businessEntity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新业务实体信息")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:update')")
    public CommonResult<Boolean> updateBusinessEntity(@PathVariable("id") Long id, @Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        reqVO.setId(id);
        businessEntityService.updateBusinessEntity(reqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除业务实体")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:delete')")
    public CommonResult<Boolean> deleteBusinessEntity(@PathVariable("id") Long id) {
        businessEntityService.deleteBusinessEntity(id);
        return success(true);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取业务实体详细信息")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<BusinessEntityRespVO> getBusinessEntity(@PathVariable("id") Long id) {
        MetadataBusinessEntityDO businessEntity = businessEntityService.getBusinessEntity(id);
        return success(BusinessEntityConvert.INSTANCE.convert(businessEntity));
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询业务实体列表")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<PageResult<BusinessEntityRespVO>> getBusinessEntityList(@Valid BusinessEntityPageReqVO pageReqVO) {
        PageResult<MetadataBusinessEntityDO> pageResult = businessEntityService.getBusinessEntityPage(pageReqVO);
        return success(BusinessEntityConvert.INSTANCE.convertPage(pageResult));
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
