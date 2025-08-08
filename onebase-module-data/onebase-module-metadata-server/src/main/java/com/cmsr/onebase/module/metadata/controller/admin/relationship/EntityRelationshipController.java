package com.cmsr.onebase.module.metadata.controller.admin.relationship;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.ParentChildRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.ParentChildRelationshipRespVO;
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
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 实体关系管理")
@RestController
@RequestMapping("/metadata/entity-relationship")
@Validated
public class EntityRelationshipController {

    @Resource
    private MetadataEntityRelationshipService entityRelationshipService;

    @PostMapping("/create")
    @Operation(summary = "创建实体间的关联关系")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:create')")
    public CommonResult<EntityRelationshipRespVO> createEntityRelationship(@Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        Long id = entityRelationshipService.createEntityRelationship(reqVO);
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(id);
        return success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "查询实体关系列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:query')")
    public CommonResult<PageResult<EntityRelationshipRespVO>> getEntityRelationshipPage(@Valid EntityRelationshipPageReqVO pageReqVO) {
        PageResult<EntityRelationshipRespVO> result = entityRelationshipService.getEntityRelationshipPage(pageReqVO);
        return success(result);
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取关系详细信息")
    @Parameter(name = "id", description = "关系ID", required = true, example = "5001")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:query')")
    public CommonResult<EntityRelationshipRespVO> getEntityRelationship(@RequestParam("id") Long id) {
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(id);
        return success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新实体关系信息")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:update')")
    public CommonResult<Boolean> updateEntityRelationship(@Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        entityRelationshipService.updateEntityRelationship(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "软删除实体关系")
    @Parameter(name = "id", description = "关系ID", required = true, example = "5001")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:delete')")
    public CommonResult<Boolean> deleteEntityRelationship(@RequestParam("id") Long id) {
        entityRelationshipService.deleteEntityRelationship(id);
        return success(true);
    }

    @PostMapping("/relationship-types")
    @Operation(summary = "获取系统支持的关系类型列表")
    public CommonResult<List<RelationshipTypeRespVO>> getRelationshipTypes() {
        List<RelationshipTypeRespVO> types = entityRelationshipService.getRelationshipTypes();
        return success(types);
    }

    @PostMapping("/cascade-types")
    @Operation(summary = "获取系统支持的级联操作类型")
    public CommonResult<List<CascadeTypeRespVO>> getCascadeTypes() {
        List<CascadeTypeRespVO> types = entityRelationshipService.getCascadeTypes();
        return success(types);
    }

    @PostMapping("/create-parent-child")
    @Operation(summary = "创建主子关系", description = "自动创建主子关系，默认使用主表id和子表parent_id关联，一对多关系，级联新增删除查询")
    @PreAuthorize("@ss.hasPermission('metadata:entity-relationship:create')")
    public CommonResult<ParentChildRelationshipRespVO> createParentChildRelationship(@Valid @RequestBody ParentChildRelationshipSaveReqVO reqVO) {
        ParentChildRelationshipRespVO result = entityRelationshipService.createParentChildRelationship(reqVO);
        return success(result);
    }

    //todo 帮我写一个添加主子关系的接口，主子关系就是一个实体关联了另一个实体，和创建关联关系接口区别为 ：1.默认用主表的 id 和子表的parent_id 关联 2.默认级联新增，删除，查询子表数据，3 默认主子关系为 一对多，上面这三点在创建主子关系的时候，会默认执行，不需要前端传值。如果前端传子表的 id 说明选择的是已经有的表，如果前端不传 id 那就意味着要新建一个子表，这块看看能不能复用新增实体的那块逻辑，具体的字段需求可以看看我给你的图片

} 