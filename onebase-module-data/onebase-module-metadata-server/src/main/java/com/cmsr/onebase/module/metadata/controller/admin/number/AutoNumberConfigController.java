package com.cmsr.onebase.module.metadata.controller.admin.number;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.service.number.AutoNumberConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import com.cmsr.onebase.module.metadata.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;

@Tag(name = "管理后台 - 自动编号配置")
@RestController
@RequestMapping("/metadata/auto-number/config")
public class AutoNumberConfigController {

    @Resource
    private AutoNumberConfigService configService;

    @PostMapping("/get")
    @Operation(summary = "按字段ID获取自动编号配置与规则")
    @PreAuthorize("@ss.hasPermission('metadata:auto-number:query')")
    public CommonResult<AutoNumberConfigWithRulesRespVO> get(@RequestParam("fieldId") Long fieldId) {
        MetadataAutoNumberConfigDO cfg = configService.getByFieldId(fieldId);
        AutoNumberConfigWithRulesRespVO resp = new AutoNumberConfigWithRulesRespVO();
        resp.setConfig(cfg);
        if (cfg != null) {
            List<MetadataAutoNumberRuleItemDO> rules = configService.listRules(cfg.getId());
            resp.setRules(rules);
        }
        return success(resp);
    }

    @PostMapping("/upsert")
    @Operation(summary = "保存/更新自动编号配置")
    @PreAuthorize("@ss.hasPermission('metadata:auto-number:update')")
    public CommonResult<Long> upsert(@Valid @RequestBody MetadataAutoNumberConfigDO req) {
        Long id = configService.upsert(req);
        return success(id);
    }
}


