package com.cmsr.onebase.module.metadata.build.controller.admin.relationship;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityWithChildrenRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.AppEntitiesRespVO;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EntityRelationshipController {

    @Resource
    private MetadataEntityRelationshipBuildService entityRelationshipService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @PostMapping("/create")
    @Operation(summary = "创建实体间的关联关系")
    public CommonResult<EntityRelationshipRespVO> createEntityRelationship(@Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        Long id = entityRelationshipService.createEntityRelationship(reqVO);
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(id);
        return success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "查询实体关系列表")
    public CommonResult<PageResult<EntityRelationshipRespVO>> getEntityRelationshipPage(@Valid @RequestBody EntityRelationshipPageReqVO pageReqVO) {
        log.info("控制器接收到的分页请求参数: entityId={}, appId={}, pageNo={}, pageSize={}",
                pageReqVO.getEntityId(), pageReqVO.getApplicationId(), pageReqVO.getPageNo(), pageReqVO.getPageSize());
        log.info("控制器参数详细信息: pageReqVO={}", pageReqVO);

        PageResult<EntityRelationshipRespVO> result = entityRelationshipService.getEntityRelationshipPage(pageReqVO);
        return success(result);
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取关系详细信息")
    @Parameter(name = "id", description = "关系ID或UUID", required = true, example = "5001")
    public CommonResult<EntityRelationshipRespVO> getEntityRelationship(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRelationshipId(id);
        EntityRelationshipRespVO result = entityRelationshipService.getEntityRelationshipDetail(resolvedId);
        return success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新实体关系信息")
    public CommonResult<Boolean> updateEntityRelationship(@Valid @RequestBody EntityRelationshipSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        entityRelationshipService.updateEntityRelationship(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "软删除实体关系")
    @Parameter(name = "id", description = "关系ID或UUID", required = true, example = "5001")
    public CommonResult<Boolean> deleteEntityRelationship(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveRelationshipId(id);
        entityRelationshipService.deleteEntityRelationship(resolvedId);
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
    public CommonResult<ParentChildRelationshipRespVO> createParentChildRelationship(@Valid @RequestBody ParentChildRelationshipSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        ParentChildRelationshipRespVO result = entityRelationshipService.createParentChildRelationship(reqVO);
        return success(result);
    }

    @PostMapping("/entity-with-children")
    @Operation(summary = "根据实体ID查询实体名称及其关联的子表信息")
    @Parameter(name = "entityId", description = "实体ID或UUID", required = true, example = "1001")
    @Parameter(name = "relationshipType", description = "关系类型筛选（ONE_TO_ONE-一对一, ONE_TO_MANY-一对多）", required = false, example = "ONE_TO_MANY")
    public CommonResult<EntityWithChildrenRespVO> getEntityWithChildren(
            @RequestParam("entityId") String entityId,
            @RequestParam(value = "relationshipType", required = false) String relationshipType) {
        // 兼容ID和UUID两种格式
        Long resolvedEntityId = idUuidConverter.resolveEntityId(entityId);
        EntityWithChildrenRespVO result = entityRelationshipService.getEntityWithChildrenById(resolvedEntityId, relationshipType);
        return success(result);
    }

    @PostMapping("/app-entities")
    @Operation(summary = "根据应用ID查询所有实体及字段信息")
    @Parameter(name = "appId", description = "应用ID", required = false, example = "1001")
    public CommonResult<AppEntitiesRespVO> getAppEntitiesWithFields(@RequestParam(value = "appId", required = false) Long appId) {
        appId = ApplicationManager.getApplicationId();
        AppEntitiesRespVO result = entityRelationshipService.getAppEntitiesWithFields(appId);
        return success(result);
    }

}
