package com.cmsr.onebase.module.metadata.build.controller.admin.entity;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintSaveReqVO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldConstraintBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体字段约束管理
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Tag(name = "管理后台 - 实体字段约束管理")
@RestController
@RequestMapping("/metadata/entity-field/constraint")
@Validated
public class EntityFieldConstraintController {

    @Resource
    private MetadataEntityFieldConstraintBuildService constraintService;

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/get")
    @Operation(summary = "按字段UUID获取约束配置")
    public CommonResult<FieldConstraintRespVO> get(@RequestParam("fieldId") String fieldUuid) {
        FieldConstraintRespVO result = constraintService.getFieldConstraintConfig(fieldUuid);
        return success(result);
    }

    @PostMapping("/upsert")
    @Operation(summary = "保存/更新约束配置")
    public CommonResult<Boolean> upsert(@Valid @RequestBody FieldConstraintSaveReqVO req) {
        constraintService.saveFieldConstraintConfig(req);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除某类型约束")
    public CommonResult<Boolean> delete(@RequestParam("fieldId") String fieldUuid,
                                        @RequestParam("constraintType") String type) {
        constraintService.delete(fieldUuid, type);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }
}


