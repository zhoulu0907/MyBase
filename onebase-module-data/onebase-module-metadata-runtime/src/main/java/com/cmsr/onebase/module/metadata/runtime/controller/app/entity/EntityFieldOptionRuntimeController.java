package com.cmsr.onebase.module.metadata.runtime.controller.app.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.runtime.service.field.MetadataEntityFieldOptionRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态 - 实体字段选项管理
 *
 * @author bty418
 * @date 2025-10-30
 */
@Tag(name = "运行态 - 实体字段选项管理")
@RestController
@RequestMapping("/metadata/entity-field/option")
@Validated
public class EntityFieldOptionRuntimeController {

    @Resource
    private MetadataEntityFieldOptionRuntimeService optionService;

    /**
     * 按字段ID获取选项列表
     *
     * @param fieldId 字段ID
     * @return 选项列表
     */
    @PostMapping("/list")
    @Operation(summary = "按字段ID获取选项列表")
    public CommonResult<List<FieldOptionRespVO>> list(@RequestParam("fieldUuid") String fieldUuid) {
        List<FieldOptionRespVO> result = optionService.getFieldOptionList(fieldUuid);
        return success(result);
    }

    /**
     * 创建选项
     *
     * @param req 选项保存请求VO
     * @return 选项ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建选项")
    public CommonResult<Long> create(@Valid @RequestBody FieldOptionSaveReqVO req) {
        Long id = optionService.createFieldOption(req);
        return success(id);
    }

    /**
     * 更新选项
     *
     * @param req 选项保存请求VO
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新选项")
    public CommonResult<Boolean> update(@Valid @RequestBody FieldOptionSaveReqVO req) {
        optionService.updateFieldOption(req);
        return success(true);
    }

    /**
     * 删除选项
     *
     * @param id 选项ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    @Operation(summary = "删除选项")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        optionService.deleteById(id);
        return success(true);
    }

    /**
     * 批量排序选项
     *
     * @param req 批量排序请求VO
     * @return 排序结果
     */
    @PostMapping("/batch-sort")
    @Operation(summary = "批量排序选项")
    public CommonResult<Boolean> batchSort(@Valid @RequestBody FieldOptionBatchSortReqVO req) {
        optionService.batchSortFieldOptions(req);
        return success(true);
    }
}

