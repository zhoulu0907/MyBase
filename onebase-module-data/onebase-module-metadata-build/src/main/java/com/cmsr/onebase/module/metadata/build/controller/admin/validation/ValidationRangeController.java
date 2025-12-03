package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRangeBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：范围")
@RestController
@RequestMapping("/metadata/validation/range")
@Validated
public class ValidationRangeController {

    @Resource private MetadataValidationRangeBuildService rangeService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段UUID获取范围校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<ValidationRangeRespVO> getByField(@RequestParam("id") String fieldUuid) {
        return success(rangeService.getByFieldIdWithRgName(fieldUuid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建范围校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationRangeSaveReqVO vo) {
        return success(rangeService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新范围校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationRangeUpdateReqVO vo) {
        rangeService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段UUID删除范围校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") String fieldUuid) {
        rangeService.deleteByFieldId(fieldUuid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取范围校验")
    @Parameter(name = "id", description = "范围校验规则主键ID", required = true)
    public CommonResult<ValidationRangeRespVO> get(@RequestParam("id") Long id) {
        return success(rangeService.getById(id));
    }

    @PostMapping("/delete")
    @Operation(summary = "按主键ID删除范围校验")
    @Parameter(name = "id", description = "范围校验规则主键ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        rangeService.deleteById(id);
        return success(true);
    }
}
