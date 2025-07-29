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
 * @author bty418
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据校验规则管理")
@RestController
@RequestMapping("/metadata/validation-rule")
@Validated
public class ValidationRuleController {

    @Resource
    private MetadataValidationRuleService validationRuleService;

    @PostMapping
    @Operation(summary = "创建数据校验规则")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:create')")
    public CommonResult<ValidationRuleRespVO> createValidationRule(@Valid @RequestBody ValidationRuleSaveReqVO reqVO) {
        Long id = validationRuleService.createValidationRule(reqVO);
        ValidationRuleRespVO result = validationRuleService.getValidationRuleDetail(id);
        return success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询校验规则列表")
    @Parameter(name = "appId", description = "应用ID", required = true, example = "12345")
    @Parameter(name = "entityId", description = "实体ID", required = false, example = "2001")
    @Parameter(name = "fieldId", description = "字段ID", required = false, example = "3001")
    @Parameter(name = "validationType", description = "校验类型", required = false, example = "FORMAT_VALIDATION")
    @Parameter(name = "keyword", description = "搜索关键词", required = false, example = "用户名")
    @Parameter(name = "pageNum", description = "页码", required = false, example = "1")
    @Parameter(name = "pageSize", description = "每页大小", required = false, example = "20")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:query')")
    public CommonResult<PageResult<ValidationRuleRespVO>> getValidationRuleList(@Valid ValidationRulePageReqVO pageReqVO) {
        PageResult<ValidationRuleRespVO> result = validationRuleService.getValidationRulePage(pageReqVO);
        return success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取校验规则详细信息")
    @Parameter(name = "id", description = "规则ID", required = true, example = "4001")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:query')")
    public CommonResult<ValidationRuleRespVO> getValidationRule(@PathVariable("id") Long id) {
        ValidationRuleRespVO result = validationRuleService.getValidationRuleDetail(id);
        return success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新校验规则信息")
    @Parameter(name = "id", description = "规则ID", required = true, example = "4001")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:update')")
    public CommonResult<Boolean> updateValidationRule(@PathVariable("id") Long id, @Valid @RequestBody ValidationRuleSaveReqVO reqVO) {
        reqVO.setId(id);
        validationRuleService.updateValidationRule(reqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除校验规则")
    @Parameter(name = "id", description = "规则ID", required = true, example = "4001")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule:delete')")
    public CommonResult<Boolean> deleteValidationRule(@PathVariable("id") Long id) {
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