package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationFormatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：格式/正则")
@RestController
@RequestMapping("/metadata/validation/format")
@Validated
public class ValidationFormatController {

    @Resource private MetadataValidationFormatService formatService;

    @PostMapping("/get-regex-by-field")
    @Operation(summary = "根据字段ID获取正则格式校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-format:query')")
    public CommonResult<MetadataValidationFormatDO> getRegexByField(@RequestParam("fieldId") Long fieldId) {
        return success(formatService.getRegexByFieldId(fieldId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建格式校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-format:create')")
    public CommonResult<Long> create(@Valid @RequestBody MetadataValidationFormatDO req) {
        return success(formatService.create(req));
    }

    @PostMapping("/update")
    @Operation(summary = "更新格式校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-format:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MetadataValidationFormatDO req) {
        formatService.update(req);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除格式校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-format:delete')")
    public CommonResult<Boolean> deleteByField(@RequestParam("fieldId") Long fieldId) {
        formatService.deleteByFieldId(fieldId);
        return success(true);
    }
}
