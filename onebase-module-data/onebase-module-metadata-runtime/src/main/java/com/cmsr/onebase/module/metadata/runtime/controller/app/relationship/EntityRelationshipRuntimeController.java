package com.cmsr.onebase.module.metadata.runtime.controller.app.relationship;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo.EntityWithChildrenRespVO;
import com.cmsr.onebase.module.metadata.runtime.service.relationship.MetadataEntityRelationshipRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态 - 实体关系管理
 *
 * @author matianyu
 * @date 2025-12-04
 */
@Tag(name = "运行态 - 实体关系管理")
@RestController
@RequestMapping("/metadata-runtime/entity-relationship")
@Validated
@Slf4j
public class EntityRelationshipRuntimeController {

    @Resource
    private MetadataEntityRelationshipRuntimeService entityRelationshipService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @PostMapping("/entity-children")
    @Operation(summary = "根据实体ID查询实体名称及其关联的子表信息（运行态）")
    @Parameter(name = "entityId", description = "实体ID或UUID", required = true, example = "1001")
    @Parameter(name = "relationshipType", description = "关系类型筛选（ONE_TO_ONE-一对一, ONE_TO_MANY-一对多）", required = false, example = "ONE_TO_MANY")
    public CommonResult<EntityWithChildrenRespVO> getEntityWithChildren(
            @RequestParam("entityId") String entityId,
            @RequestParam(value = "relationshipType", required = false) String relationshipType) {
        Long resolvedEntityId = idUuidConverter.resolveEntityId(entityId);
        EntityWithChildrenRespVO result = entityRelationshipService.getEntityWithChildrenById(resolvedEntityId, relationshipType);
        return success(result);
    }

}
