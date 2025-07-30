package com.cmsr.onebase.module.metadata.controller.admin.relationship;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.service.relationship.MetadataEntityRelationshipService;
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

/**
 * 管理后台 - 实体关系管理
 *
 * @author bty418
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 实体关系管理")
@RestController
@RequestMapping("/metadata/entity-relationship")
@Validated
public class EntityRelationshipController {

    @Resource
    private MetadataEntityRelationshipService entityRelationshipService;

    @PostMapping
    @Operation(summary = "创建实体间的关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:create')")
    public CommonResult<EntityRelationshipRespVO> createEntityRelationship(@Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        Long id = entityRelationshipService.createEntityRelationship(reqVO);
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(id);
        return success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "查询实体关系列表")
    @Parameter(name = "appId", description = "应用ID", required = true, example = "12345")
    @Parameter(name = "sourceEntityId", description = "源实体ID", required = false, example = "2001")
    @Parameter(name = "targetEntityId", description = "目标实体ID", required = false, example = "2002")
    @Parameter(name = "relationshipType", description = "关系类型", required = false, example = "ONE_TO_MANY")
    @Parameter(name = "pageNum", description = "页码", required = false, example = "1")
    @Parameter(name = "pageSize", description = "每页大小", required = false, example = "20")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:query')")
    public CommonResult<PageResult<EntityRelationshipRespVO>> getEntityRelationshipList(@Valid EntityRelationshipPageReqVO pageReqVO) {
        PageResult<EntityRelationshipRespVO> result = entityRelationshipService.getEntityRelationshipPage(pageReqVO);
        return success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取关系详细信息")
    @Parameter(name = "id", description = "关系ID", required = true, example = "5001")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:query')")
    public CommonResult<EntityRelationshipRespVO> getEntityRelationship(@PathVariable("id") Long id) {
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(id);
        return success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新实体关系信息")
    @Parameter(name = "id", description = "关系ID", required = true, example = "5001")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:update')")
    public CommonResult<Boolean> updateEntityRelationship(@PathVariable("id") Long id, @Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        reqVO.setId(id);
        entityRelationshipService.updateEntityRelationship(reqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除实体关系")
    @Parameter(name = "id", description = "关系ID", required = true, example = "5001")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:delete')")
    public CommonResult<Boolean> deleteEntityRelationship(@PathVariable("id") Long id) {
        entityRelationshipService.deleteEntityRelationship(id);
        return success(true);
    }

    @GetMapping("/relationship-types")
    @Operation(summary = "获取系统支持的关系类型列表")
    public CommonResult<List<RelationshipTypeRespVO>> getRelationshipTypes() {
        List<RelationshipTypeRespVO> types = entityRelationshipService.getRelationshipTypes();
        return success(types);
    }

    @GetMapping("/cascade-types")
    @Operation(summary = "获取系统支持的级联操作类型")
    public CommonResult<List<CascadeTypeRespVO>> getCascadeTypes() {
        List<CascadeTypeRespVO> types = entityRelationshipService.getCascadeTypes();
        return success(types);
    }

} 