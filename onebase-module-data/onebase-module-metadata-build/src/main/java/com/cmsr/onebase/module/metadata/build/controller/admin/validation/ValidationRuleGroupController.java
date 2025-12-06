package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSimpleRespVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRuleGroupBuildService;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    private MetadataValidationRuleGroupBuildService validationRuleGroupService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    // 该控制器已精简为仅分页和统一操作入口，不再提供单独的新增/修改/删除/详情接口
    // 新增/修改/删除/详情请使用各具体校验类型 Controller 或自定义验证 Controller

    @PostMapping("/page")
    @Operation(summary = "获得校验规则分组分页")
    public CommonResult<PageResult<ValidationRuleGroupSimpleRespVO>> getValidationRuleGroupPage(@Valid @RequestBody ValidationRuleGroupPageReqVO pageReqVO) {
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换（可选字段）
        String entityUuid = idUuidConverter.resolveEntityUuidOptional(pageReqVO.getEntityUuid(), pageReqVO.getEntityId());
        pageReqVO.setEntityUuid(entityUuid);
        return success(validationRuleGroupService.getValidationRuleGroupPageSimple(pageReqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "统一删除规则组及其下规则（按ID或UUID）")
    @Parameter(name = "id", description = "规则组ID或UUID", required = true)
    public CommonResult<Boolean> unifiedDelete(@RequestParam("id") String id) {
        Long groupId = idUuidConverter.resolveRuleGroupId(id);
        validationRuleGroupService.deleteValidationRuleGroup(groupId);
        return success(true);
    }

}
