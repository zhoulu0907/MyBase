package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRulePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationRuleService;
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
 * 管理后台 - 数据校验规则管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据校验规则管理")
@RestController
@RequestMapping("/metadata/validation-rule")
@Validated
public class ValidationRuleController {

    @Resource
    private MetadataValidationRuleService validationRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建数据校验规则")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:create')")
    public CommonResult<ValidationRuleRespVO> createValidationRule(@Valid @RequestBody ValidationRuleSaveReqVO reqVO) {
        Long id = validationRuleService.createValidationRule(reqVO);
        ValidationRuleRespVO result = validationRuleService.getValidationRuleDetail(id);
        return success(result);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询校验规则列表")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:query')")
    public CommonResult<PageResult<ValidationRuleRespVO>> getValidationRulePage(@Valid ValidationRulePageReqVO pageReqVO) {
        PageResult<ValidationRuleRespVO> result = validationRuleService.getValidationRulePage(pageReqVO);
        return success(result);
    }

    @GetMapping("/get")
    @Operation(summary = "根据ID获取校验规则详细信息")
    @Parameter(name = "id", description = "规则ID", required = true, example = "4001")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:query')")
    public CommonResult<ValidationRuleRespVO> getValidationRule(@RequestParam("id") Long id) {
        ValidationRuleRespVO result = validationRuleService.getValidationRuleDetail(id);
        return success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新校验规则信息")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:update')")
    public CommonResult<Boolean> updateValidationRule(@Valid @RequestBody ValidationRuleSaveReqVO reqVO) {
        validationRuleService.updateValidationRule(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "软删除校验规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "4001")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:delete')")
    public CommonResult<Boolean> deleteValidationRule(@RequestParam("id") Long id) {
        validationRuleService.deleteValidationRule(id);
        return success(true);
    }

    @GetMapping("/validation-types")
    @Operation(summary = "获取系统支持的校验类型列表")
    public CommonResult<List<ValidationTypeConfigRespVO>> getValidationTypes() {
        List<ValidationTypeConfigRespVO> types = validationRuleService.getValidationTypes();
        return success(types);
    }

} 