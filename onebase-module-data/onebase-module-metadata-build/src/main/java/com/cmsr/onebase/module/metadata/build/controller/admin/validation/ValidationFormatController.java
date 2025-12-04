package com.cmsr.onebase.module.metadata.build.controller.admin.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationFormatBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 校验规则：格式/正则")
@RestController
@RequestMapping("/metadata/validation/format")
@Validated
public class ValidationFormatController {

    @Resource private MetadataValidationFormatBuildService formatService;

    @PostMapping("/get-regex-by-field")
    @Operation(summary = "根据字段UUID获取正则格式校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<ValidationFormatRespVO> getRegexByField(@RequestParam("id") String fieldUuid) {
        return success(formatService.getRegexByFieldIdWithRgName(fieldUuid));
    }

    @GetMapping("/get")
    @Operation(summary = "根据主键ID获取格式校验")
    @Parameter(name = "id", description = "校验规则ID", required = true)
    public CommonResult<ValidationFormatRespVO> get(@RequestParam("id") String id) {
        return success(formatService.getById(Long.parseLong(id)));
    }

    @PostMapping("/create")
    @Operation(summary = "创建格式校验")
    public CommonResult<Long> create(@Valid @RequestBody ValidationFormatSaveReqVO vo) {
        return success(formatService.create(vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新格式校验")
    public CommonResult<Boolean> update(@Valid @RequestBody ValidationFormatUpdateReqVO vo) {
        formatService.update(vo);
        return success(true);
    }

    @PostMapping("/delete-by-field")
    @Operation(summary = "按字段UUID删除格式校验")
    @Parameter(name = "id", description = "字段UUID", required = true)
    public CommonResult<Boolean> deleteByField(@RequestParam("id") String fieldUuid) {
        formatService.deleteByFieldId(fieldUuid);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "根据主键ID删除格式校验")
    @Parameter(name = "id", description = "校验规则ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") String id) {
        formatService.deleteById(Long.parseLong(id));
        return success(true);
    }
}
