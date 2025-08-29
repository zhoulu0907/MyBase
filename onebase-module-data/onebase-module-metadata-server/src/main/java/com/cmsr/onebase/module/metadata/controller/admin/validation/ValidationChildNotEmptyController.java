package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationChildNotEmptyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：子表非空")
@RestController
@RequestMapping("/metadata/validation/child-not-empty")
@Validated
public class ValidationChildNotEmptyController {

    @Resource private MetadataValidationChildNotEmptyService childNotEmptyService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取子表非空校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-child-not-empty:query')")
    public CommonResult<ValidationChildNotEmptyRespVO> getByField(@RequestParam("fieldId") Long fieldId) {
        return success(childNotEmptyService.getByFieldIdWithRgName(fieldId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建子表非空校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-child-not-empty:create')")
    public CommonResult<Long> create(@Valid @RequestBody ValidationChildNotEmptySaveReqVO vo) {
        return success(childNotEmptyService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新子表非空校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-child-not-empty:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationChildNotEmptyUpdateReqVO vo) {
        childNotEmptyService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除子表非空校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-child-not-empty:delete')")
    public CommonResult<Boolean> deleteByField(@RequestParam("fieldId") Long fieldId) {
        childNotEmptyService.deleteByFieldId(fieldId);
        return success(true);
    }
}
