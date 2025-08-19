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
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchSaveRespVO;
import com.cmsr.onebase.module.metadata.convert.entity.EntityFieldConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldOptionService;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldConstraintService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
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
    private MetadataEntityFieldService entityFieldService;
    @Resource
    private MetadataEntityFieldOptionService fieldOptionService;
    @Resource
    private MetadataEntityFieldConstraintService fieldConstraintService;

    @PostMapping("/field-types")
    @Operation(summary = "获取系统支持的字段类型列表")
    public CommonResult<List<FieldTypeConfigRespVO>> getFieldTypes() {
        List<FieldTypeConfigRespVO> fieldTypes = entityFieldService.getFieldTypes();
        return success(fieldTypes);
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量为业务实体创建字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:create')")
    public CommonResult<EntityFieldBatchCreateRespVO> batchCreateEntityFields(@Valid @RequestBody EntityFieldBatchCreateReqVO reqVO) {
        EntityFieldBatchCreateRespVO result = entityFieldService.batchCreateEntityFields(reqVO);
        return success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "为业务实体创建新字段")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:create')")
    public CommonResult<EntityFieldRespVO> createEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        Long id = entityFieldService.createEntityField(reqVO);
        // 读取字段用于补充 appId/runMode
        MetadataEntityFieldDO entityField = entityFieldService.getEntityField(String.valueOf(id));
        // 同步处理选项与约束（整体替换）
        if (reqVO.getOptions() != null) {
            // 清空旧数据（新建字段一般无旧数据，但为幂等处理）
            fieldOptionService.deleteByFieldId(id);
            for (var opt : reqVO.getOptions()) {
                com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO();
                d.setFieldId(id);
                d.setOptionLabel(opt.getOptionLabel());
                d.setOptionValue(opt.getOptionValue());
                d.setOptionOrder(opt.getOptionOrder());
                d.setIsEnabled(opt.getIsEnabled());
                d.setDescription(opt.getDescription());
                d.setAppId(entityField != null ? entityField.getAppId() : null);
                fieldOptionService.create(d);
            }
        }
        if (reqVO.getConstraints() != null) {
            // 全量替换：先删再插
            fieldConstraintService.deleteByFieldId(id);
            var c = reqVO.getConstraints();
            if (c.getMinLength() != null && c.getMaxLength() != null && c.getMinLength() > c.getMaxLength()) {
                throw new IllegalArgumentException("最小长度不能大于最大长度");
            }
            if (c.getMinLength() != null || c.getMaxLength() != null || c.getLengthEnabled() != null || (c.getLengthPrompt() != null && !c.getLengthPrompt().isEmpty())) {
                var d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                d.setFieldId(id);
                d.setConstraintType("LENGTH_RANGE");
                d.setMinLength(c.getMinLength());
                d.setMaxLength(c.getMaxLength());
                d.setPromptMessage(c.getLengthPrompt());
                d.setIsEnabled(c.getLengthEnabled());
                d.setRunMode(entityField != null ? entityField.getRunMode() : 0);
                d.setAppId(entityField != null ? entityField.getAppId() : null);
                fieldConstraintService.upsert(d);
            }
            if (c.getRegexPattern() != null || c.getRegexEnabled() != null || (c.getRegexPrompt() != null && !c.getRegexPrompt().isEmpty())) {
                var d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                d.setFieldId(id);
                d.setConstraintType("REGEX");
                d.setRegexPattern(c.getRegexPattern());
                d.setPromptMessage(c.getRegexPrompt());
                d.setIsEnabled(c.getRegexEnabled());
                d.setRunMode(entityField != null ? entityField.getRunMode() : 0);
                d.setAppId(entityField != null ? entityField.getAppId() : null);
                fieldConstraintService.upsert(d);
            }
        }
        return success(EntityFieldConvert.INSTANCE.convert(entityField));
    }

    @PostMapping("/list")
    @Operation(summary = "查询指定实体的字段列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<List<EntityFieldRespVO>> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqVO reqVO) {
        // 将Controller层的VO转换为Service层的VO
        EntityFieldQueryVO queryVO = EntityFieldConvert.INSTANCE.convertVOToQueryVO(reqVO);
        List<MetadataEntityFieldDO> list = entityFieldService.getEntityFieldListByConditions(queryVO);
        List<EntityFieldRespVO> respList = EntityFieldConvert.INSTANCE.convertList(list);
        // 为每个字段补充选项与约束信息（按需）
        for (int i = 0; i < list.size(); i++) {
            MetadataEntityFieldDO f = list.get(i);
            EntityFieldRespVO v = respList.get(i);
            // 单/多选返回选项
            if ("SINGLE_SELECT".equalsIgnoreCase(f.getFieldType()) || "MULTI_SELECT".equalsIgnoreCase(f.getFieldType())) {
                var options = fieldOptionService.listByFieldId(f.getId());
                if (options != null && !options.isEmpty()) {
                    java.util.List<FieldOptionRespVO> ov = new java.util.ArrayList<>();
                    for (var o : options) {
                        FieldOptionRespVO item = new FieldOptionRespVO();
                        item.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
                        item.setFieldId(o.getFieldId());
                        item.setOptionLabel(o.getOptionLabel());
                        item.setOptionValue(o.getOptionValue());
                        item.setOptionOrder(o.getOptionOrder());
                        item.setIsEnabled(o.getIsEnabled());
                        item.setDescription(o.getDescription());
                        ov.add(item);
                    }
                    v.setOptions(ov);
                }
            }
            var constraints = fieldConstraintService.listByFieldId(f.getId());
            if (constraints != null && !constraints.isEmpty()) {
                FieldConstraintRespVO cr = new FieldConstraintRespVO();
                for (var c : constraints) {
                    if ("LENGTH_RANGE".equalsIgnoreCase(c.getConstraintType())) {
                        cr.setLengthEnabled(c.getIsEnabled());
                        cr.setMinLength(c.getMinLength());
                        cr.setMaxLength(c.getMaxLength());
                        cr.setLengthPrompt(c.getPromptMessage());
                    } else if ("REGEX".equalsIgnoreCase(c.getConstraintType())) {
                        cr.setRegexEnabled(c.getIsEnabled());
                        cr.setRegexPattern(c.getRegexPattern());
                        cr.setRegexPrompt(c.getPromptMessage());
                    }
                }
                v.setConstraints(cr);
            }
        }
        return success(respList);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询指定实体的字段列表")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<PageResult<EntityFieldRespVO>> getEntityFieldPage(@Valid @RequestBody EntityFieldPageReqVO pageReqVO) {
        PageResult<MetadataEntityFieldDO> pageResult = entityFieldService.getEntityFieldPage(pageReqVO);
        return success(EntityFieldConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取字段详细信息")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:query')")
    public CommonResult<EntityFieldDetailRespVO> getEntityField(@RequestParam("id") String id) {
        EntityFieldDetailRespVO entityField = entityFieldService.getEntityFieldDetail(id);
        return success(entityField);
    }

    @PostMapping("/batch-update")
    @Operation(summary = "批量更新实体字段信息")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<EntityFieldBatchUpdateRespVO> batchUpdateEntityFields(@Valid @RequestBody EntityFieldBatchUpdateReqVO reqVO) {
        EntityFieldBatchUpdateRespVO result = entityFieldService.batchUpdateEntityFields(reqVO);
        return success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新实体字段信息")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<Boolean> updateEntityField(@Valid @RequestBody EntityFieldSaveReqVO reqVO) {
        entityFieldService.updateEntityField(reqVO);
        // 同步处理选项与约束（整体替换）
        if (reqVO.getId() != null) {
            Long fieldId = Long.valueOf(reqVO.getId());
            MetadataEntityFieldDO entityField = entityFieldService.getEntityField(String.valueOf(fieldId));
            if (reqVO.getOptions() != null) {
                fieldOptionService.deleteByFieldId(fieldId);
                for (var opt : reqVO.getOptions()) {
                    com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO();
                    d.setFieldId(fieldId);
                    d.setOptionLabel(opt.getOptionLabel());
                    d.setOptionValue(opt.getOptionValue());
                    d.setOptionOrder(opt.getOptionOrder());
                    d.setIsEnabled(opt.getIsEnabled());
                    d.setDescription(opt.getDescription());
                    d.setAppId(entityField != null ? entityField.getAppId() : null);
                    fieldOptionService.create(d);
                }
            }
            if (reqVO.getConstraints() != null) {
                fieldConstraintService.deleteByFieldId(fieldId);
                var c = reqVO.getConstraints();
                if (c.getMinLength() != null && c.getMaxLength() != null && c.getMinLength() > c.getMaxLength()) {
                    throw new IllegalArgumentException("最小长度不能大于最大长度");
                }
                if (c.getMinLength() != null || c.getMaxLength() != null || c.getLengthEnabled() != null || (c.getLengthPrompt() != null && !c.getLengthPrompt().isEmpty())) {
                    var d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                    d.setFieldId(fieldId);
                    d.setConstraintType("LENGTH_RANGE");
                    d.setMinLength(c.getMinLength());
                    d.setMaxLength(c.getMaxLength());
                    d.setPromptMessage(c.getLengthPrompt());
                    d.setIsEnabled(c.getLengthEnabled());
                    d.setRunMode(entityField != null ? entityField.getRunMode() : 0);
                    d.setAppId(entityField != null ? entityField.getAppId() : null);
                    fieldConstraintService.upsert(d);
                }
                if (c.getRegexPattern() != null || c.getRegexEnabled() != null || (c.getRegexPrompt() != null && !c.getRegexPrompt().isEmpty())) {
                    var d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                    d.setFieldId(fieldId);
                    d.setConstraintType("REGEX");
                    d.setRegexPattern(c.getRegexPattern());
                    d.setPromptMessage(c.getRegexPrompt());
                    d.setIsEnabled(c.getRegexEnabled());
                    d.setRunMode(entityField != null ? entityField.getRunMode() : 0);
                    d.setAppId(entityField != null ? entityField.getAppId() : null);
                    fieldConstraintService.upsert(d);
                }
            }
        }
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "软删除实体字段")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:delete')")
    public CommonResult<Boolean> deleteEntityField(@RequestParam("id") Long id) {
        entityFieldService.deleteEntityField(String.valueOf(id));
        return success(true);
    }

    @PostMapping("/batch-sort")
    @Operation(summary = "批量更新字段排序")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<Boolean> batchSortEntityFields(@Valid @RequestBody EntityFieldBatchSortReqVO reqVO) {
        entityFieldService.batchSortEntityFields(reqVO);
        return success(true);
    }

    @PostMapping("/batch-save")
    @Operation(summary = "批量保存实体字段（增删改）")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field:update')")
    public CommonResult<EntityFieldBatchSaveRespVO> batchSave(@Valid @RequestBody EntityFieldBatchSaveReqVO reqVO) {
        EntityFieldBatchSaveRespVO resp = entityFieldService.batchSaveEntityFields(reqVO);
        return success(resp);
    }
}
