package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.service.DataServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.FileServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.UserServiceImpl;
import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;
import org.pf4j.CompoundPluginDescriptorFinder;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginManager;
import org.pf4j.PropertiesPluginDescriptorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 插件运行时自动配置
 * <p>
 * 配置PF4J的PluginManager和相关Bean。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Configuration
@ConditionalOnProperty(prefix = "onebase.plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PluginProperties.class)
public class PluginRuntimeAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PluginRuntimeAutoConfiguration.class);

    /**
     * 配置PF4J PluginManager
     *
     * @param properties 插件配置属性
     * @return PluginManager
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginManager pluginManager(PluginProperties properties) {
        Path pluginsPath = Paths.get(properties.getPluginsDir());
        log.info("初始化插件管理器，插件目录: {}", pluginsPath.toAbsolutePath());
        log.info("插件目录是否存在: {}", pluginsPath.toFile().exists());
        log.info("插件目录是否为目录: {}", pluginsPath.toFile().isDirectory());

        // 创建自定义的 DefaultPluginManager，支持 plugin.properties
        // 不使用 SpringPluginManager 避免 SpringExtensionFactory 的 wrapper null 问题
        DefaultPluginManager pluginManager = new DefaultPluginManager(pluginsPath) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                // 组合使用 PropertiesPluginDescriptorFinder 和 ManifestPluginDescriptorFinder
                // 优先使用 plugin.properties，其次使用 MANIFEST.MF
                return new CompoundPluginDescriptorFinder()
                        .add(new PropertiesPluginDescriptorFinder())
                        .add(new ManifestPluginDescriptorFinder());
            }
        };

        if (properties.isAutoLoad()) {
            // 加载插件
            pluginManager.loadPlugins();
            log.info("已加载 {} 个插件", pluginManager.getPlugins().size());

            if (properties.isAutoStart()) {
                // 启动插件
                pluginManager.startPlugins();
                log.info("已启动 {} 个插件", pluginManager.getStartedPlugins().size());
            }
        }

        return pluginManager;
    }

    /**
     * 配置OneBase插件管理器
     *
     * @param pluginManager PF4J PluginManager
     * @return OneBasePluginManager
     */
    @Bean
    @ConditionalOnMissingBean
    public OneBasePluginManager oneBasePluginManager(PluginManager pluginManager) {
        return new OneBasePluginManager(pluginManager);
    }

    /**
     * 配置插件上下文工厂
     *
     * @param applicationContext Spring ApplicationContext
     * @return PluginContextFactory
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginContextFactory pluginContextFactory(ApplicationContext applicationContext) {
        return new PluginContextFactory(applicationContext);
    }

    /**
     * 配置事件分发器
     *
     * @param pluginManager 插件管理器
     * @param contextFactory 上下文工厂
     * @return EventDispatcher
     */
    @Bean
    @ConditionalOnMissingBean
    public EventDispatcher eventDispatcher(OneBasePluginManager pluginManager, 
                                           PluginContextFactory contextFactory) {
        return new EventDispatcher(pluginManager, contextFactory);
    }

    /**
     * 配置数据服务
     *
     * @return DataService
     */
    @Bean
    @ConditionalOnMissingBean(DataService.class)
    public DataService dataService() {
        return new DataServiceImpl();
    }

    /**
     * 配置用户服务
     *
     * @return UserService
     */
    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService() {
        return new UserServiceImpl();
    }

    /**
     * 配置文件服务
     *
     * @return FileService
     */
    @Bean
    @ConditionalOnMissingBean(FileService.class)
    public FileService fileService() {
        return new FileServiceImpl();
    }
}
