package com.cmsr.onebase.module.metadata.build.controller.admin.number;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberStateBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 自动编号状态与重置")
@RestController
@RequestMapping("/metadata/auto-number/state")
@Validated
public class AutoNumberStateController {

    @Resource
    private AutoNumberStateBuildService stateService;

    @PostMapping("/next")
    @Operation(summary = "获取下一编号计数（仅测试/预览用途）")
    public CommonResult<Long> next(@RequestParam("configId") Long configId) {
        long val = stateService.nextNumber(configId, java.time.LocalDateTime.now());
        return success(val);
    }

    @PostMapping("/reset")
    @Operation(summary = "手动重置到指定下一编号")
    public CommonResult<Boolean> reset(@RequestParam("configId") Long configId,
                                       @RequestParam("periodKey") String periodKey,
                                       @RequestParam("nextValue") Long nextValue,
                                       @RequestParam(value = "operator", required = false) Long operator,
                                       @RequestParam(value = "reason", required = false) String reason) {
        stateService.reset(configId, periodKey, nextValue, operator, reason);
        return success(true);
    }
}


