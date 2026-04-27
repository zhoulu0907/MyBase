package com.cmsr.onebase.plugin.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.build.service.PluginConfigService;
import com.cmsr.onebase.plugin.build.service.PluginInfoService;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigDetailRespVO;
import com.cmsr.onebase.plugin.core.constant.PluginStatusConstants;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 插件上下文")
@RestController
@RequestMapping("/plugin/context")
@Validated
@Slf4j
public class PluginContextController {

    @Resource
    private PluginConfigService pluginConfigService;

    @Resource
    private PluginInfoService pluginInfoService;

    @Resource
    private PluginInfoRepository pluginInfoRepository;

    @Resource
    private PluginProperties pluginProperties;

    @GetMapping("/config/plain")
    @Operation(summary = "获取指定插件的配置Map（值已按类型处理）")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "onebase-plugin-ocr"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    public CommonResult<Map<String, Object>> getConfigPlain(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        PluginConfigDetailRespVO detail = pluginConfigService.getConfigDetail(pluginId, pluginVersion);
        Map<String, Object> plain = new HashMap<>();
        if (detail != null && detail.getConfigs() != null) {
            detail.getConfigs().forEach((k, v) -> plain.put(k, v != null ? v.getConfigValue() : null));
        }
        return success(plain);
    }

    @GetMapping("/manifest")
    @PermitAll
    @Operation(summary = "获取所有可用插件的前端资源清单")
    public CommonResult<List<Map<String, Object>>> getPluginManifest() {
        List<Map<String, Object>> manifest = new ArrayList<>();
        // 从数据库获取已启用的插件列表
        List<PluginInfoDO> plugins = pluginInfoRepository.list(
                QueryWrapper.create().eq(PluginInfoDO::getStatus, PluginStatusConstants.ENABLED)
        );

        String contextPath = pluginProperties.getFrontendContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }

        for (PluginInfoDO plugin : plugins) {
            String pluginId = plugin.getPluginId();
            String version = plugin.getPluginVersion();
            String dirName = "frontend-" + pluginId + "-" + version;

            Path frontendDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), dirName);
            if (!Files.exists(frontendDir)) {
                // 尝试懒加载提取前端资源
                try {
                    pluginInfoService.preparePluginFrontend(plugin);
                } catch (Exception e) {
                    log.error("懒加载提取前端资源失败: pluginId={}, version={}", pluginId, version, e);
                }

                if (!Files.exists(frontendDir)) {
                    continue;
                }
            }

            Map<String, Object> info = new HashMap<>();
            info.put("pluginId", pluginId);
            info.put("version", version);
            String baseUrl = contextPath + dirName + "/";
            // 检查是否存在嵌套frontend目录
            if (Files.exists(frontendDir.resolve("frontend"))) {
                baseUrl += "frontend/";
            }
            info.put("baseUrl", baseUrl);

            if (Files.exists(frontendDir.resolve("remoteEntry.js"))) {
                info.put("entry", "remoteEntry.js");
                info.put("type", "module-federation");
            } else if (Files.exists(frontendDir.resolve("index.html"))) {
                info.put("entry", "index.html");
                info.put("type", "iframe");
            } else if (Files.exists(frontendDir.resolve("frontend/index.html"))) {
                info.put("entry", "frontend/index.html");
                info.put("type", "iframe");
            } else if (Files.exists(frontendDir.resolve("frontend/remoteEntry.js"))) {
                info.put("entry", "frontend/remoteEntry.js");
                info.put("type", "module-federation");
            } else {
                info.put("type", "static");
            }

            manifest.add(info);
        }

        return success(manifest);
    }
}
