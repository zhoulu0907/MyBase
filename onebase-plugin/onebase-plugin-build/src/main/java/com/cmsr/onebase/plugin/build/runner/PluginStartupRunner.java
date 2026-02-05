package com.cmsr.onebase.plugin.build.runner;

import com.cmsr.onebase.plugin.build.service.PluginInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 插件启动加载器
 * <p>
 * 在应用启动时，检查并准备所有已启用插件的前端资源。
 * 确保插件前端资源在启动后立即可用。
 * </p>
 *
 * @author onebase
 */
@Component
@Slf4j
public class PluginStartupRunner implements ApplicationRunner {

    @Resource
    private PluginInfoService pluginInfoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始执行插件启动加载任务...");
        try {
            pluginInfoService.prepareAllPluginFrontends();
        } catch (Exception e) {
            log.error("插件启动加载任务执行失败", e);
        }
        log.info("插件启动加载任务执行结束");
    }
}
