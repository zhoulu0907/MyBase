package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.convert.validation.ValidationRuleGroupConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationRuleGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 校验规则分组管理
 *
 * @author bty418
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 校验规则分组管理")
@RestController
@RequestMapping("/metadata/validation-rule-group")
@Validated
public class ValidationRuleGroupController {

    @Resource
    private MetadataValidationRuleGroupService validationRuleGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建校验规则分组")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:create')")
    public CommonResult<Long> createValidationRuleGroup(@Valid @RequestBody ValidationRuleGroupSaveReqVO createReqVO) {
        return success(validationRuleGroupService.createValidationRuleGroup(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新校验规则分组")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:update')")
    public CommonResult<Boolean> updateValidationRuleGroup(@Valid @RequestBody ValidationRuleGroupSaveReqVO updateReqVO) {
        validationRuleGroupService.updateValidationRuleGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除校验规则分组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:delete')")
    public CommonResult<Boolean> deleteValidationRuleGroup(@RequestParam("id") Long id) {
        validationRuleGroupService.deleteValidationRuleGroup(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得校验规则分组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:query')")
    public CommonResult<ValidationRuleGroupRespVO> getValidationRuleGroup(@RequestParam("id") Long id) {
        MetadataValidationRuleGroupDO ruleGroup = validationRuleGroupService.getValidationRuleGroup(id);
        ValidationRuleGroupRespVO respVO = ValidationRuleGroupConvert.INSTANCE.convert(ruleGroup);
        
        // 获取规则定义的二维数组结构
        respVO.setValueRules(validationRuleGroupService.buildValueRulesStructure(id));
        
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得校验规则分组分页")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:query')")
    public CommonResult<PageResult<ValidationRuleGroupRespVO>> getValidationRuleGroupPage(@Valid ValidationRuleGroupPageReqVO pageReqVO) {
        PageResult<MetadataValidationRuleGroupDO> pageResult = validationRuleGroupService.getValidationRuleGroupPage(pageReqVO);
        return success(ValidationRuleGroupConvert.INSTANCE.convertPage(pageResult));
    }

}
