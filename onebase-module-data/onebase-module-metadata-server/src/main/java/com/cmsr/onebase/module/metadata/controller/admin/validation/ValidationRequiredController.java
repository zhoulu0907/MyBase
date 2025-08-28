package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationRequiredService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：必填")
@RestController
@RequestMapping("/metadata/validation/required")
@Validated
public class ValidationRequiredController {

    @Resource private MetadataValidationRequiredService requiredService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取必填校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-required:query')")
    public CommonResult<MetadataValidationRequiredDO> getByField(@RequestParam("fieldId") Long fieldId) {
        return success(requiredService.getByFieldId(fieldId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建必填校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-required:create')")
    public CommonResult<Long> create(@Valid @RequestBody ValidationRequiredSaveReqVO vo) {
        return success(requiredService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新必填校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-required:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MetadataValidationRequiredDO req) {
        requiredService.update(req);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除必填校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-required:delete')")
    public CommonResult<Boolean> deleteByField(@RequestParam("fieldId") Long fieldId) {
        requiredService.deleteByFieldId(fieldId);
        return success(true);
    }
}
