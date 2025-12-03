package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationUniqueBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：唯一")
@RestController
@RequestMapping("/metadata/validation/unique")
@Validated
public class ValidationUniqueController {

    @Resource private MetadataValidationUniqueBuildService uniqueService;

    @PostMapping("/get-by-field")
    @Operation(summary = "根据字段ID获取唯一性校验")
    @Parameter(name = "id", description = "字段ID", required = true)
    public CommonResult<ValidationUniqueRespVO> getByField(@RequestParam("id") Long id) {
        return success(uniqueService.getByFieldIdWithRgName(id));
    }
    @PostMapping("/create")
    @Operation(summary = "创建唯一性校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationUniqueSaveReqVO vo) {
        return success(uniqueService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新唯一性校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationUniqueUpdateReqVO vo) {
        uniqueService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段删除唯一性校验")
    @Parameter(name = "id", description = "字段ID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") Long id) {
        uniqueService.deleteByFieldId(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取唯一性校验")
    @Parameter(name = "id", description = "唯一性校验规则主键ID", required = true)
    public CommonResult<ValidationUniqueRespVO> get(@RequestParam("id") Long id) {
        return success(uniqueService.getById(id));
    }

    @PostMapping("/delete")
    @Operation(summary = "按主键ID删除唯一性校验")
    @Parameter(name = "id", description = "唯一性校验规则主键ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        uniqueService.deleteById(id);
        return success(true);
    }
}
