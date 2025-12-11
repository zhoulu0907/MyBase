package com.cmsr.onebase.module.metadata.runtime.controller.app.number;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.number.vo.AutoNumberConfigWithRulesRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.runtime.service.number.AutoNumberConfigRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态 - 自动编号配置
 *
 * @author bty418
 * @date 2025-10-30
 */
@Tag(name = "运行态 - 自动编号配置")
@RestController
@RequestMapping("/metadata/auto-number/config")
@Validated
public class AutoNumberConfigRuntimeController {

    @Resource
    private AutoNumberConfigRuntimeService configService;

    /**
     * 按字段ID获取自动编号配置与规则
     *
     * @param fieldId 字段ID
     * @return 自动编号配置与规则响应VO
     */
    @PostMapping("/get")
    @Operation(summary = "按字段ID获取自动编号配置与规则")
    public CommonResult<AutoNumberConfigWithRulesRespVO> get(@RequestParam("fieldId") Long fieldId) {
        AutoNumberConfigWithRulesRespVO result = configService.getAutoNumberConfigWithRules(fieldId);
        return success(result);
    }

    /**
     * 保存/更新自动编号配置
     *
     * @param req 自动编号配置请求
     * @return 配置ID
     */
    @PostMapping("/upsert")
    @Operation(summary = "保存/更新自动编号配置")
    public CommonResult<Long> upsert(@Valid @RequestBody MetadataAutoNumberConfigDO req) {
        Long id = configService.saveAutoNumberConfig(req);
        return success(id);
    }
}

