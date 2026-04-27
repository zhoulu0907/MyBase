package com.cmsr.onebase.plugin.runtime.startup;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.runtime.loader.PluginFileManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 插件启动加载器
 * <p>
 * 应用启动时，自动从数据库查询已启用的插件，
 * 如果本地文件不存在则从MinIO下载，最后加载并启动插件。
 * </p>
 *
 * @author onebase
 * @date 2026-01-22
 */
@Component
@Slf4j
public class PluginStartupRunner implements ApplicationRunner {

    @Resource
    private PluginInfoRepository pluginInfoRepository;

    @Resource
    private PluginFileManager pluginFileManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始执行插件启动加载任务...");
        try {
            // 1. 查询所有状态为已启用的插件
            // 状态：0 关闭，1 开启

            TenantManager.withoutTenantCondition(() -> {
                // 1. 查询所有状态为已启用的插件
                // 状态：0 关闭，1 开启
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .eq(PluginInfoDO::getStatus, 1);
                List<PluginInfoDO> enabledPlugins = pluginInfoRepository.list(queryWrapper);

                if (enabledPlugins == null || enabledPlugins.isEmpty()) {
                    log.info("未发现已启用的插件，跳过加载。");
                    return;
                }

                log.info("发现 {} 个已启用的插件，准备加载...", enabledPlugins.size());

                for (PluginInfoDO plugin : enabledPlugins) {
                    loadPluginSafe(plugin);
                }
            });

            log.info("插件启动加载任务执行完成。");
        } catch (Exception e) {
            log.error("插件启动加载任务执行异常", e);
        }
    }

    /**
     * 安全加载单个插件
     *
     * @param plugin 插件信息
     */
    private void loadPluginSafe(PluginInfoDO plugin) {
        String pluginId = plugin.getPluginId();
        String version = plugin.getPluginVersion();
        Long packageId = plugin.getPluginPackage();

        try {
            // 2. 检查本地是否存在
            boolean exists = pluginFileManager.isPluginDirExists(pluginId, version);

            if (!exists) {
                // 不存在，下载并加载（内部会自动启动）
                log.info("插件本地文件不存在，开始下载: {} - {}", pluginId, version);
                if (packageId == null) {
                    log.warn("插件包ID为空，无法下载: {} - {}", pluginId, version);
                    return;
                }
                pluginFileManager.downloadAndExtractPlugin(pluginId, version, packageId);
            } else {
                // 存在，直接加载（内部会自动启动）
                log.info("插件本地文件已存在，直接加载: {} - {}", pluginId, version);
                pluginFileManager.loadPlugin(pluginId, version);
            }
        } catch (Exception e) {
            log.error("加载插件失败: {} - {}", pluginId, version, e);
            // 捕获异常，避免影响其他插件加载
        }
    }
}
