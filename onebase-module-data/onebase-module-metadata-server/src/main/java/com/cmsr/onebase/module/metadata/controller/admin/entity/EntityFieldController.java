package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchSortReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.convert.entity.EntityFieldConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实体字段管理")
@RestController
@RequestMapping("/metadata/entity-field")
@Validated
public class EntityFieldController {

    @Resource
    private MetadataEntityFieldService entityFieldService;

    @GetMapping("/field-types")
    @Operation(summary = "获取系统支持的字段类型列表")
    public CommonResult<List<FieldTypeConfigRespVO>> getFieldTypes() {
        List<FieldTypeConfigRespVO> fieldTypes = entityFieldService.getFieldTypes();
        return success(fieldTypes);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量为业务实体创建字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:create')")
    public CommonResult<EntityFieldBatchCreateRespVO> batchCreateEntityFields(@Valid @RequestBody EntityFieldBatchCreateReqVO reqVO) {
        EntityFieldBatchCreateRespVO result = entityFieldService.batchCreateEntityFields(reqVO);
        return success(result);
    }

    @PostMapping
    @Operation(summary = "为业务实体创建新字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:create')")
    public CommonResult<EntityFieldRespVO> createEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        Long id = entityFieldService.createEntityField(reqVO);
        MetadataEntityFieldDO entityField = entityFieldService.getEntityField(id);
        return success(EntityFieldConvert.INSTANCE.convert(entityField));
    }

    @GetMapping("/list")
    @Operation(summary = "查询指定实体的字段列表")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1024")
    @Parameter(name = "isSystemField", description = "是否系统字段", required = false, example = "false")
    @Parameter(name = "keyword", description = "搜索关键词", required = false, example = "name")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<List<EntityFieldRespVO>> getEntityFieldList(
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "isSystemField", required = false) Boolean isSystemField,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<MetadataEntityFieldDO> list = entityFieldService.getEntityFieldListByConditions(entityId, isSystemField, keyword);
        return success(EntityFieldConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取字段详细信息")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<EntityFieldDetailRespVO> getEntityField(@PathVariable("id") Long id) {
        EntityFieldDetailRespVO entityField = entityFieldService.getEntityFieldDetail(id);
        return success(entityField);
    }

    @PutMapping("/batch")
    @Operation(summary = "批量更新实体字段信息")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<EntityFieldBatchUpdateRespVO> batchUpdateEntityFields(@Valid @RequestBody EntityFieldBatchUpdateReqVO reqVO) {
        EntityFieldBatchUpdateRespVO result = entityFieldService.batchUpdateEntityFields(reqVO);
        return success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新实体字段信息")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<Boolean> updateEntityField(@PathVariable("id") Long id, @Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        reqVO.setId(id);
        entityFieldService.updateEntityField(reqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除实体字段")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:delete')")
    public CommonResult<Boolean> deleteEntityField(@PathVariable("id") Long id) {
        entityFieldService.deleteEntityField(id);
        return success(true);
    }

    @PutMapping("/batch-sort")
    @Operation(summary = "批量更新字段排序")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<Boolean> batchSortEntityFields(@Valid @RequestBody EntityFieldBatchSortReqVO reqVO) {
        entityFieldService.batchSortEntityFields(reqVO);
        return success(true);
    }

}
