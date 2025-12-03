package com.cmsr.onebase.module.metadata.build.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体字段选项管理
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Tag(name = "管理后台 - 实体字段选项管理")
@RestController
@RequestMapping("/metadata/entity-field/option")
@Validated
public class EntityFieldOptionController {

    @Resource
    private MetadataEntityFieldOptionBuildService optionService;

    @PostMapping("/list")
    @Operation(summary = "按字段UUID获取选项列表")
    public CommonResult<List<FieldOptionRespVO>> list(@RequestParam("fieldId") String fieldUuid) {
        List<FieldOptionRespVO> result = optionService.getFieldOptionList(fieldUuid);
        return success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "创建选项")
    public CommonResult<Long> create(@Valid @RequestBody FieldOptionSaveReqVO req) {
        Long id = optionService.createFieldOption(req);
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新选项")
    public CommonResult<Boolean> update(@Valid @RequestBody FieldOptionSaveReqVO req) {
        optionService.updateFieldOption(req);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除选项")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        optionService.deleteById(id);
        return success(true);
    }

    @PostMapping("/batch-sort")
    @Operation(summary = "批量排序选项")
    public CommonResult<Boolean> batchSort(@Valid @RequestBody FieldOptionBatchSortReqVO req) {
        optionService.batchSortFieldOptions(req);
        return success(true);
    }
}


