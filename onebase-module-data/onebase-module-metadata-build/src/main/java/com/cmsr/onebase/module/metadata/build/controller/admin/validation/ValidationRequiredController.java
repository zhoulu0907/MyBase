package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRequiredBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：必填")
@RestController
@RequestMapping("/metadata/validation/required")
@Validated
public class ValidationRequiredController {

    @Resource private MetadataValidationRequiredBuildService requiredService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段UUID获取必填校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<ValidationRequiredRespVO> getByField(@RequestParam("id") String fieldUuid) {
        return success(requiredService.getByFieldIdWithRgName(fieldUuid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建必填校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationRequiredSaveReqVO vo) {
        return success(requiredService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新必填校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationRequiredUpdateReqVO vo) {
        requiredService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段UUID删除必填校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") String fieldUuid) {
        requiredService.deleteByFieldId(fieldUuid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取必填校验")
    @Parameter(name = "id", description = "必填校验规则主键ID", required = true)
    public CommonResult<ValidationRequiredRespVO> get(@RequestParam("id") Long id) {
        return success(requiredService.getById(id));
    }

    @PostMapping("/delete")
    @Operation(summary = "按主键ID删除必填校验")
    @Parameter(name = "id", description = "必填校验规则主键ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        requiredService.deleteById(id);
        return success(true);
    }
}
