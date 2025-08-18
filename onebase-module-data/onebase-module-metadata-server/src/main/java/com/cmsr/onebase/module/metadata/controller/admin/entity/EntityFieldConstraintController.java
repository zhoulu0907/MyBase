package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldConstraintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实体字段约束管理")
@RestController
@RequestMapping("/metadata/entity-field/constraint")
public class EntityFieldConstraintController {

    @Resource
    private MetadataEntityFieldConstraintService constraintService;

    @PostMapping("/get")
    @Operation(summary = "按字段ID获取约束配置")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-constraint:query')")
    public CommonResult<FieldConstraintRespVO> get(@RequestParam("fieldId") Long fieldId) {
        List<MetadataEntityFieldConstraintDO> list = constraintService.listByFieldId(fieldId);
        FieldConstraintRespVO resp = new FieldConstraintRespVO();
        if (list != null) {
            list.forEach(c -> {
                if ("LENGTH_RANGE".equalsIgnoreCase(c.getConstraintType())) {
                    resp.setLengthEnabled(c.getIsEnabled());
                    resp.setMinLength(c.getMinLength());
                    resp.setMaxLength(c.getMaxLength());
                    resp.setLengthPrompt(c.getPromptMessage());
                } else if ("REGEX".equalsIgnoreCase(c.getConstraintType())) {
                    resp.setRegexEnabled(c.getIsEnabled());
                    resp.setRegexPattern(c.getRegexPattern());
                    resp.setRegexPrompt(c.getPromptMessage());
                }
            });
        }
        return success(resp);
    }

    @PostMapping("/upsert")
    @Operation(summary = "保存/更新约束配置")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-constraint:update')")
    public CommonResult<Boolean> upsert(@Valid @RequestBody FieldConstraintSaveReqVO req) {
        // 简单校验：LENGTH_RANGE 需校验区间合理
        if ("LENGTH_RANGE".equalsIgnoreCase(req.getConstraintType())) {
            Integer min = req.getMinLength();
            Integer max = req.getMaxLength();
            if (min != null && max != null && min > max) {
                throw new IllegalArgumentException("最小长度不能大于最大长度");
            }
        }
        MetadataEntityFieldConstraintDO d = new MetadataEntityFieldConstraintDO();
        d.setFieldId(req.getFieldId());
        d.setConstraintType(req.getConstraintType());
        d.setMinLength(req.getMinLength());
        d.setMaxLength(req.getMaxLength());
        d.setRegexPattern(req.getRegexPattern());
        d.setPromptMessage(req.getPromptMessage());
        d.setIsEnabled(req.getIsEnabled());
        d.setRunMode(req.getRunMode());
        d.setAppId(req.getAppId());
        constraintService.upsert(d);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除某类型约束")
    @PreAuthorize("@ss.hasPermission('metadata:entity-field-constraint:delete')")
    public CommonResult<Boolean> delete(@RequestParam("fieldId") Long fieldId,
                                        @RequestParam("constraintType") String type) {
        constraintService.delete(fieldId, type);
        return success(true);
    }
}


