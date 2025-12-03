package com.cmsr.onebase.module.metadata.build.controller.admin.number;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberRuleBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 自动编号规则")
@RestController
@RequestMapping("/metadata/auto-number/rule")
@Validated
public class AutoNumberRuleController {

    @Resource
    private AutoNumberRuleBuildService ruleService;

    @PostMapping("/list")
    @Operation(summary = "按配置ID获取规则项列表")
    public CommonResult<List<MetadataAutoNumberRuleItemDO>> list(@RequestParam("configId") Long configId) {
        return success(ruleService.listByConfig(configId));
    }

    @PostMapping("/add")
    @Operation(summary = "添加规则项")
    public CommonResult<Long> add(@Valid @RequestBody MetadataAutoNumberRuleItemDO req) {
        return success(ruleService.add(req));
    }

    @PostMapping("/update")
    @Operation(summary = "更新规则项")
    public CommonResult<Boolean> update(@Valid @RequestBody MetadataAutoNumberRuleItemDO req) {
        ruleService.update(req);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除规则项")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        ruleService.delete(id);
        return success(true);
    }

    @PostMapping("/sort")
    @Operation(summary = "批量排序规则项")
    public CommonResult<Boolean> sort(@RequestParam("configId") Long configId,
                                      @RequestBody List<MetadataAutoNumberRuleItemDO> items) {
        ruleService.batchSort(configId, items);
        return success(true);
    }
}


