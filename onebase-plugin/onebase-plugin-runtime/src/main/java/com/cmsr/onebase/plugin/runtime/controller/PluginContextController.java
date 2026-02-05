package com.cmsr.onebase.plugin.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.common.util.PluginPackageUtil;
import com.cmsr.onebase.plugin.core.constant.PluginStatusConstants;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.service.PluginContextService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@Slf4j
public class PluginContextController {

    @Resource
    private PluginContextService pluginContextService;

    @Resource
    private PluginInfoRepository pluginInfoRepository;

    @Resource
    private PluginProperties pluginProperties;

    @Resource
    private FileApi fileApi;

    @GetMapping("/manifest")
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
            
            // 检查前端目录是否存在
            Path frontendDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), dirName);
            if (!Files.exists(frontendDir)) {
                // 尝试懒加载提取前端资源
                try {
                    downloadAndExtractFrontend(plugin);
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
            
            // 构建访问基路径
            String baseUrl = contextPath + dirName + "/";
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

    private void downloadAndExtractFrontend(PluginInfoDO plugin) {
        Long fileId = plugin.getPluginPackage();
        if (fileId == null) {
            log.warn("插件包文件ID为空: pluginId={}", plugin.getPluginId());
            return;
        }

        log.info("开始懒加载下载插件: pluginId={}, fileId={}", plugin.getPluginId(), fileId);

        CommonResult<byte[]> result = fileApi.getFileContentBytes(fileId);
        if (!result.isSuccess() || result.getData() == null) {
            log.error("下载插件文件失败: {}", result.getMsg());
            return;
        }

        Path tempZipFile = null;
        try {
            tempZipFile = Files.createTempFile("plugin-lazy-", ".zip");
            try (OutputStream os = Files.newOutputStream(tempZipFile)) {
                os.write(result.getData());
            }

            String dirName = "frontend-" + plugin.getPluginId() + "-" + plugin.getPluginVersion();
            Path frontendTargetDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), dirName);

            PluginPackageUtil.extractFrontendZip(tempZipFile, frontendTargetDir);

        } catch (IOException e) {
            log.error("解压插件文件失败", e);
        } finally {
            PluginPackageUtil.deleteFile(tempZipFile);
        }
    }

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
