package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationLengthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：长度")
@RestController
@RequestMapping("/metadata/validation/length")
@Validated
public class ValidationLengthController {

    @Resource private MetadataValidationLengthService lengthService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取长度校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-length:query')")
    public CommonResult<ValidationLengthRespVO> getByFieldId(@RequestParam("fieldId") Long fieldId) {
        return success(lengthService.getByFieldIdWithRgName(fieldId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建长度校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-length:create')")
    public CommonResult<Long> create(@Valid @RequestBody ValidationLengthSaveReqVO vo) {
        return success(lengthService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新长度校验")
    @PreAuthorize("@ss.hasPermission('metadata:validation-length:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationLengthUpdateReqVO vo) {
        lengthService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除长度校验")
    @Parameter(name = "fieldId", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-length:delete')")
    public CommonResult<Boolean> deleteByField(@RequestParam("fieldId") Long fieldId) {
        lengthService.deleteByFieldId(fieldId);
        return success(true);
    }
}
