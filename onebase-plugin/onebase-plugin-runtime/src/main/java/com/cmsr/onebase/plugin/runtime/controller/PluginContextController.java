package com.cmsr.onebase.plugin.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.service.PluginContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 插件上下文控制器
 * <p>
 * 提供插件上下文信息的查询接口，包括租户ID、应用ID和插件配置
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Tag(name = "管理后台 - 插件上下文")
@RestController
@RequestMapping("/plugin/context")
@Validated
public class PluginContextController {

    @Resource
    private PluginContextService pluginContextService;

    @GetMapping("/tenant-id")
    @Operation(summary = "获取当前租户ID")
    public CommonResult<Long> getTenantId() {
        Long tenantId = pluginContextService.getTenantId();
        return success(tenantId);
    }

    @GetMapping("/application-id")
    @Operation(summary = "获取当前应用ID")
    public CommonResult<Long> getApplicationId() {
        Long applicationId = pluginContextService.getApplicationId();
        return success(applicationId);
    }

    @GetMapping("/config")
    @Operation(summary = "获取指定插件的全部配置")
    public CommonResult<Map<String, Object>> getConfig(
            @Parameter(description = "插件ID", required = true) @RequestParam("pluginId") String pluginId,
            @Parameter(description = "插件版本", required = true) @RequestParam("version") String version) {
        Map<String, Object> config = pluginContextService.getConfig(pluginId, version);
        return success(config);
    }
}
