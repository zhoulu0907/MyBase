package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 自定义验证规则组管理
 * 迁移自原 ValidationRuleGroupController 的增删改查能力，仅作用于 SELF_DEFINED 类型
 *
 * @author bty418
 * @date 2025-09-08
 */
@Tag(name = "管理后台 - 自定义验证规则组管理")
@RestController
@RequestMapping("/metadata/validation-self-defined")
@Validated
public class ValidationSelfDefinedController {

    @Resource
    private MetadataValidationRuleGroupService validationRuleGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建自定义校验规则组")
    @PreAuthorize("@ss.hasPermission('metadata:validation-self-defined:create')")
    public CommonResult<Long> create(@Valid @RequestBody ValidationRuleGroupSaveReqVO createReqVO) {
        createReqVO.setValidationType("SELF_DEFINED");
        return success(validationRuleGroupService.createValidationRuleGroup(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新自定义校验规则组")
    @PreAuthorize("@ss.hasPermission('metadata:validation-self-defined:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationRuleGroupSaveReqVO updateReqVO) {
        updateReqVO.setValidationType("SELF_DEFINED");
        validationRuleGroupService.updateValidationRuleGroup(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除自定义校验规则组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-self-defined:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        validationRuleGroupService.deleteValidationRuleGroup(id);
        return success(true);
    }

    @PostMapping("/get")
    @Operation(summary = "获得自定义校验规则组详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:validation-self-defined:query')")
    public CommonResult<ValidationRuleGroupRespVO> get(@RequestParam("id") Long id) {
        MetadataValidationRuleGroupDO ruleGroup = validationRuleGroupService.getValidationRuleGroup(id);
        ValidationRuleGroupRespVO respVO = ValidationRuleGroupConvert.INSTANCE.convert(ruleGroup);
        if (respVO != null) {
            respVO.setValueRules(validationRuleGroupService.buildValueRulesStructure(id));
        }
        return success(respVO);
    }
}
