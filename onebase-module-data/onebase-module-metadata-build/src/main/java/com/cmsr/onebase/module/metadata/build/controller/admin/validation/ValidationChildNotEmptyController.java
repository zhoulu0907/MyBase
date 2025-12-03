package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationChildNotEmptyBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：子表非空")
@RestController
@RequestMapping("/metadata/validation/child-not-empty")
@Validated
public class ValidationChildNotEmptyController {

    @Resource private MetadataValidationChildNotEmptyBuildService childNotEmptyService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取子表非空校验")
    @Parameter(name = "id", description = "字段ID", required = true)
    public CommonResult<ValidationChildNotEmptyRespVO> getByField(@RequestParam("id") Long id) {
        return success(childNotEmptyService.getByFieldIdWithRgName(id));
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取子表非空校验")
    @Parameter(name = "id", description = "校验规则ID", required = true)
    public CommonResult<ValidationChildNotEmptyRespVO> get(@RequestParam("id") Long id) {
        return success(childNotEmptyService.getById(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建子表非空校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationChildNotEmptySaveReqVO vo) {
        return success(childNotEmptyService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新子表非空校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationChildNotEmptyUpdateReqVO vo) {
        childNotEmptyService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除子表非空校验")
    @Parameter(name = "id", description = "字段ID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") Long id) {
        childNotEmptyService.deleteByFieldId(id);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "根据主键ID删除子表非空校验")
    @Parameter(name = "id", description = "校验规则ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        childNotEmptyService.deleteById(id);
        return success(true);
    }
}
