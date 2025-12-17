package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationLengthBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：长度")
@RestController
@RequestMapping("/metadata/validation/length")
@Validated
public class ValidationLengthController {

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @Resource private MetadataValidationLengthBuildService lengthService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段UUID获取长度校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<ValidationLengthRespVO> getByFieldId(@RequestParam("id") String fieldUuid) {
        return success(lengthService.getByFieldIdWithRgName(fieldUuid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建长度校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationLengthSaveReqVO vo) {
        return success(lengthService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新长度校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationLengthUpdateReqVO vo) {
        lengthService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段UUID删除长度校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") String fieldUuid) {
        lengthService.deleteByFieldId(fieldUuid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "根据规则组ID获取长度校验")
    @Parameter(name = "id", description = "规则组ID（支持ID或UUID）", required = true)
    public CommonResult<ValidationLengthRespVO> get(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        return success(lengthService.getById(resolvedId));
    }

    @PostMapping("/delete")
    @Operation(summary = "按规则组ID删除长度校验")
    @Parameter(name = "id", description = "规则组ID（支持ID或UUID）", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRuleGroupId(id);
        lengthService.deleteById(resolvedId);
        return success(true);
    }
}
