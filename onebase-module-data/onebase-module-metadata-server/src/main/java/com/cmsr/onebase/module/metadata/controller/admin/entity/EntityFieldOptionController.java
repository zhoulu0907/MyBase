package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实体字段选项管理")
@RestController
@RequestMapping("/metadata/entity-field/option")
public class EntityFieldOptionController {

    @Resource
    private MetadataEntityFieldOptionService optionService;

    @PostMapping("/list")
    @Operation(summary = "按字段ID获取选项列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-option:query')")
    public CommonResult<List<FieldOptionRespVO>> list(@RequestParam("fieldId") Long fieldId) {
        List<MetadataEntityFieldOptionDO> list = optionService.listByFieldId(fieldId);
        List<FieldOptionRespVO> resp = list.stream().map(this::toResp).collect(Collectors.toList());
        return success(resp);
    }

    @PostMapping("/create")
    @Operation(summary = "创建选项")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-option:create')")
    public CommonResult<Long> create(@Valid @RequestBody FieldOptionSaveReqVO req) {
        MetadataEntityFieldOptionDO doObj = toDO(req);
        Long id = optionService.create(doObj);
        return success(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新选项")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-option:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody FieldOptionSaveReqVO req) {
        MetadataEntityFieldOptionDO doObj = toDO(req);
        if (req.getId() != null) {
            doObj.setId(Long.valueOf(req.getId()));
        }
        optionService.update(doObj);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除选项")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-option:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        optionService.deleteById(id);
        return success(true);
    }

    @PostMapping("/batch-sort")
    @Operation(summary = "批量排序选项")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-option:update')")
    public CommonResult<Boolean> batchSort(@Valid @RequestBody FieldOptionBatchSortReqVO req) {
        List<MetadataEntityFieldOptionDO> list = req.getItems().stream().map(it -> {
            MetadataEntityFieldOptionDO d = new MetadataEntityFieldOptionDO();
            d.setId(it.getId());
            d.setOptionOrder(it.getOptionOrder());
            return d;
        }).collect(Collectors.toList());
        optionService.batchSort(req.getFieldId(), list);
        return success(true);
    }

    private FieldOptionRespVO toResp(MetadataEntityFieldOptionDO o) {
        FieldOptionRespVO v = new FieldOptionRespVO();
        v.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
        v.setFieldId(o.getFieldId());
        v.setOptionLabel(o.getOptionLabel());
        v.setOptionValue(o.getOptionValue());
        v.setOptionOrder(o.getOptionOrder());
        v.setIsEnabled(o.getIsEnabled());
        v.setDescription(o.getDescription());
        return v;
    }

    private MetadataEntityFieldOptionDO toDO(FieldOptionSaveReqVO r) {
        MetadataEntityFieldOptionDO d = new MetadataEntityFieldOptionDO();
        d.setFieldId(r.getFieldId());
        d.setOptionLabel(r.getOptionLabel());
        d.setOptionValue(r.getOptionValue());
        d.setOptionOrder(r.getOptionOrder());
        d.setIsEnabled(r.getIsEnabled());
        d.setDescription(r.getDescription());
        d.setAppId(r.getAppId());
        return d;
    }
}


