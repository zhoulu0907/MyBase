package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationUniqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：唯一")
@RestController
@RequestMapping("/metadata/validation/unique")
@Validated
public class ValidationUniqueController {

    @Resource private MetadataValidationUniqueService uniqueService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取唯一性校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-unique:query')")
    public CommonResult<MetadataValidationUniqueDO> getByField(@RequestParam("fieldId") Long fieldId) {
        return success(uniqueService.getByFieldId(fieldId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建唯一性校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-unique:create')")
    public CommonResult<Long> create(@Valid @RequestBody MetadataValidationUniqueDO req) {
        return success(uniqueService.create(req));
    }

    @PostMapping("/update")
    @Operation(summary = "更新唯一性校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-unique:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MetadataValidationUniqueDO req) {
        uniqueService.update(req);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除唯一性校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-unique:delete')")
    public CommonResult<Boolean> deleteByField(@RequestParam("fieldId") Long fieldId) {
        uniqueService.deleteByFieldId(fieldId);
        return success(true);
    }
}
