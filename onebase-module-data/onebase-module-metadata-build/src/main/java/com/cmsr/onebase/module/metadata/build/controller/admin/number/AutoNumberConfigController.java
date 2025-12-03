package com.cmsr.onebase.module.metadata.build.controller.admin.number;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberConfigBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 自动编号配置
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Tag(name = "管理后台 - 自动编号配置")
@RestController
@RequestMapping("/metadata/auto-number/config")
@Validated
public class AutoNumberConfigController {

    @Resource
    private AutoNumberConfigBuildService configService;

    @PostMapping("/get")
    @Operation(summary = "按字段UUID获取自动编号配置与规则")
    public CommonResult<AutoNumberConfigWithRulesRespVO> get(@RequestParam("fieldId") String fieldUuid) {
        AutoNumberConfigWithRulesRespVO result = configService.getAutoNumberConfigWithRules(fieldUuid);
        return success(result);
    }

    @PostMapping("/upsert")
    @Operation(summary = "保存/更新自动编号配置")
    public CommonResult<Long> upsert(@Valid @RequestBody MetadataAutoNumberConfigDO req) {
        Long id = configService.saveAutoNumberConfig(req);
        return success(id);
    }
}


