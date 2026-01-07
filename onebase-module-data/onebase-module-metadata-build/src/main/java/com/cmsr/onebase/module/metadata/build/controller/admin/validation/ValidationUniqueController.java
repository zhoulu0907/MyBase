package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationUniqueBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：唯一")
@RestController
@RequestMapping("/metadata/validation/unique")
@Validated
public class ValidationUniqueController {

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @Resource private MetadataValidationUniqueBuildService uniqueService;

    @Resource
    ApplicationEventPublisher applicationEventPublisher;
    
    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段UUID获取唯一性校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<ValidationUniqueRespVO> getByField(@RequestParam("id") String fieldUuid) {
        return success(uniqueService.getByFieldIdWithRgName(fieldUuid));
    }
    @PostMapping("/create")
    @Operation(summary = "创建唯一性校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationUniqueSaveReqVO vo) {
        Long id = uniqueService.create(vo);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新唯一性校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationUniqueUpdateReqVO vo) {
        uniqueService.update(vo);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段UUID删除唯一性校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") String fieldUuid) {
        uniqueService.deleteByFieldId(fieldUuid);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取唯一性校验")
    @Parameter(name = "id", description = "唯一性校验规则主键ID（支持ID或UUID）", required = true)
    public CommonResult<ValidationUniqueRespVO> get(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        return success(uniqueService.getById(resolvedId));
    }

    @PostMapping("/delete")
    @Operation(summary = "按主键ID删除唯一性校验")
    @Parameter(name = "id", description = "唯一性校验规则主键ID（支持ID或UUID）", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        uniqueService.deleteById(resolvedId);
        applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
        return success(true);
    }
}
