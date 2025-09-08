package com.cmsr.onebase.module.metadata.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSimpleRespVO;
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

    // 该控制器已精简为仅分页和统一操作入口，不再提供单独的新增/修改/删除/详情接口
    // 新增/修改/删除/详情请使用各具体校验类型 Controller 或自定义验证 Controller

    @PostMapping("/page")
    @Operation(summary = "获得校验规则分组分页")
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:query')")
    public CommonResult<PageResult<ValidationRuleGroupSimpleRespVO>> getValidationRuleGroupPage(@Valid @RequestBody ValidationRuleGroupPageReqVO pageReqVO) {
        // TODO: 汇总七种校验类型分页（当前先复用原逻辑）后续可在Service新增聚合方法
        return success(validationRuleGroupService.getValidationRuleGroupPageSimple(pageReqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "统一删除规则组及其下规则（按ID）")
    @Parameter(name = "id", description = "规则组ID", required = true)
    @PreAuthorize("@ss.hasPermission('metadata:validation-rule-group:delete')")
    public CommonResult<Boolean> unifiedDelete(@RequestParam("id") Long id) {
        validationRuleGroupService.deleteValidationRuleGroup(id);
        return success(true);
    }

}
