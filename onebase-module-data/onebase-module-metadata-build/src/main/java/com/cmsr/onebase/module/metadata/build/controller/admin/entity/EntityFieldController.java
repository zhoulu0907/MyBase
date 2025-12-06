package com.cmsr.onebase.module.metadata.build.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchCreateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchCreateRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSortReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchUpdateRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldBatchSaveRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldValidationTypesReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldValidationTypesRespVO;
import org.modelmapper.ModelMapper;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体字段管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 实体字段管理")
@RestController
@RequestMapping("/metadata/entity-field")
@Validated
public class EntityFieldController {

    @Resource
    private MetadataEntityFieldBuildService entityFieldService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @PostMapping("/field-types")
    @Operation(summary = "获取系统支持的字段类型列表")
    public CommonResult<List<FieldTypeConfigRespVO>> getFieldTypes() {
        List<FieldTypeConfigRespVO> fieldTypes = entityFieldService.getFieldTypes();
        return success(fieldTypes);
    }
    @PostMapping("/batch-create")
    @Operation(summary = "批量为业务实体创建字段")
    public CommonResult<EntityFieldBatchCreateRespVO> batchCreateEntityFields(@Valid @RequestBody EntityFieldBatchCreateReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        EntityFieldBatchCreateRespVO result = entityFieldService.batchCreateEntityFields(reqVO);
        return success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "为业务实体创建新字段")
    public CommonResult<EntityFieldRespVO> createEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        EntityFieldRespVO result = entityFieldService.createEntityFieldWithRelated(reqVO);
        return success(result);
    }
    @PostMapping("/list")
    @Operation(summary = "查询指定实体的字段列表")
    public CommonResult<List<EntityFieldRespVO>> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqVO reqVO) {
        List<EntityFieldRespVO> result = entityFieldService.getEntityFieldListWithRelated(reqVO);
        return success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询指定实体的字段列表")
    public CommonResult<PageResult<EntityFieldRespVO>> getEntityFieldPage(@Valid @RequestBody EntityFieldPageReqVO pageReqVO) {
        PageResult<EntityFieldRespVO> result = entityFieldService.getEntityFieldPageWithRelated(pageReqVO);
        return success(result);
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取字段详细信息")
    @Parameter(name = "id", description = "字段ID或UUID", required = true, example = "1024")
    public CommonResult<EntityFieldDetailRespVO> getEntityField(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveFieldId(id);
        EntityFieldDetailRespVO entityField = entityFieldService.getEntityFieldDetailWithFullConfig(String.valueOf(resolvedId));
        return success(entityField);
    }
    @PostMapping("/batch-update")
    @Operation(summary = "批量更新实体字段信息")
    public CommonResult<EntityFieldBatchUpdateRespVO> batchUpdateEntityFields(@Valid @RequestBody EntityFieldBatchUpdateReqVO reqVO) {
        EntityFieldBatchUpdateRespVO result = entityFieldService.batchUpdateEntityFields(reqVO);
        return success(result);
    }
    @PostMapping("/update")
    @Operation(summary = "更新实体字段信息")
    public CommonResult<Boolean> updateEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        Boolean result = entityFieldService.updateEntityFieldWithRelated(reqVO);
        return success(result);
    }
    @PostMapping("/delete")
    @Operation(summary = "软删除实体字段")
    @Parameter(name = "id", description = "字段ID或UUID", required = true, example = "1024")
    public CommonResult<Boolean> deleteEntityField(@RequestParam("id") String id) {
        Long resolvedId = idUuidConverter.resolveFieldId(id);
        entityFieldService.deleteEntityField(String.valueOf(resolvedId));
        return success(true);
    }

    @PostMapping("/batch-sort")
    @Operation(summary = "批量更新字段排序")
    public CommonResult<Boolean> batchSortEntityFields(@Valid @RequestBody EntityFieldBatchSortReqVO reqVO) {
        entityFieldService.batchSortEntityFields(reqVO);
        return success(true);
    }
    @PostMapping("/batch-save")
    @Operation(summary = "批量保存实体字段（增删改）")
    public CommonResult<EntityFieldBatchSaveRespVO> batchSave(@Valid @RequestBody EntityFieldBatchSaveReqVO reqVO) {
        // 从请求头获取应用ID
        reqVO.setApplicationId(String.valueOf(ApplicationManager.getApplicationId()));
        EntityFieldBatchSaveRespVO resp = entityFieldService.batchSaveEntityFields(reqVO);
        return success(resp);
    }

    @PostMapping("/validation-types/query")
    @Operation(summary = "批量查询字段可选校验类型")
    public CommonResult<List<EntityFieldValidationTypesRespVO>> getFieldValidationTypes(
            @Valid @RequestBody EntityFieldValidationTypesReqVO reqVO) {
        List<EntityFieldValidationTypesRespVO> result = entityFieldService.getFieldValidationTypes(reqVO);
        return success(result);
    }
}
