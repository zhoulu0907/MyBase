package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
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

@Tag(name = "管理后台 - 实体字段")
@RestController
@RequestMapping("/metadata/entity-field")
@Validated
public class EntityFieldController {

    @Resource
    private MetadataEntityFieldService entityFieldService;

    @PostMapping("/create")
    @Operation(summary = "新增实体字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:create')")
    public CommonResult<Long> createEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        Long id = entityFieldService.createEntityField(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改实体字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<Boolean> updateEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        entityFieldService.updateEntityField(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实体字段")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:delete')")
    public CommonResult<Boolean> deleteEntityField(@RequestParam("id") Long id) {
        entityFieldService.deleteEntityField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实体字段详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<EntityFieldRespVO> getEntityField(@RequestParam("id") Long id) {
        MetadataEntityFieldDO entityField = entityFieldService.getEntityField(id);
        return success(EntityFieldConvert.INSTANCE.convert(entityField));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实体字段分页列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<PageResult<EntityFieldRespVO>> getEntityFieldPage(@Valid EntityFieldPageReqVO pageReqVO) {
        PageResult<MetadataEntityFieldDO> pageResult = entityFieldService.getEntityFieldPage(pageReqVO);
        return success(EntityFieldConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得实体字段列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<List<EntityFieldRespVO>> getEntityFieldList() {
        List<MetadataEntityFieldDO> list = entityFieldService.getEntityFieldList();
        return success(EntityFieldConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-by-entity")
    @Operation(summary = "根据实体获得字段列表")
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<List<EntityFieldRespVO>> getEntityFieldListByEntityId(@RequestParam("entityId") Long entityId) {
        List<MetadataEntityFieldDO> list = entityFieldService.getEntityFieldListByEntityId(entityId);
        return success(EntityFieldConvert.INSTANCE.convertList(list));
    }

}
