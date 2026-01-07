package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import org.modelmapper.ModelMapper;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRuleGroupBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
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
    private MetadataIdUuidConverter idUuidConverter;

    @Resource
    private MetadataValidationRuleGroupBuildService validationRuleGroupService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/create")
    @Operation(summary = "创建自定义校验规则组")
    public CommonResult<Long> create(@Valid @RequestBody ValidationRuleGroupSaveReqVO createReqVO) {
        createReqVO.setValidationType("SELF_DEFINED");
        // 修复：正确处理 entityId 和 entityUuid 的转换
        String entityUuid = idUuidConverter.resolveEntityUuid(createReqVO.getEntityUuid(), createReqVO.getEntityId());
        createReqVO.setEntityUuid(entityUuid);
        Long id = validationRuleGroupService.createValidationRuleGroup(createReqVO);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新自定义校验规则组")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationRuleGroupSaveReqVO updateReqVO) {
        updateReqVO.setValidationType("SELF_DEFINED");
        // 修复：正确处理 entityId 和 entityUuid 的转换
        String entityUuid = idUuidConverter.resolveEntityUuidOptional(updateReqVO.getEntityUuid(), updateReqVO.getEntityId());
        if (entityUuid != null) {
            updateReqVO.setEntityUuid(entityUuid);
        }
        validationRuleGroupService.updateValidationRuleGroup(updateReqVO);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除自定义校验规则组")
    @Parameter(name = "id", description = "编号（支持ID或UUID）", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        validationRuleGroupService.deleteValidationRuleGroup(resolvedId);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }

    @PostMapping("/get")
    @Operation(summary = "获得自定义校验规则组详情")
    @Parameter(name = "id", description = "编号（支持ID或UUID）", required = true, example = "1024")
    public CommonResult<ValidationRuleGroupRespVO> get(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        MetadataValidationRuleGroupDO ruleGroup = validationRuleGroupService.getValidationRuleGroup(resolvedId);
        ValidationRuleGroupRespVO respVO = modelMapper.map(ruleGroup, ValidationRuleGroupRespVO.class);
        if (respVO != null) {
            respVO.setValueRules(validationRuleGroupService.buildValueRulesStructure(resolvedId));
        }
        return success(respVO);
    }
}
