package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.controller.PluginController;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.service.CacheServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.DataServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.FileServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.UserServiceImpl;
import com.cmsr.onebase.plugin.service.CacheService;
import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

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
@ComponentScan(basePackageClasses = {PluginController.class})
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

        SpringPluginManager pluginManager = new SpringPluginManager(pluginsPath);

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
     * 配置缓存服务
     *
     * @param redisTemplate Redis模板（可选）
     * @return CacheService
     */
    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public CacheService cacheService(StringRedisTemplate redisTemplate) {
        return new CacheServiceImpl(redisTemplate);
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
