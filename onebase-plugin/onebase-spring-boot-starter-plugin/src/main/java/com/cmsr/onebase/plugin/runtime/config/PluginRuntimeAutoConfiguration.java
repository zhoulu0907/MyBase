package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.service.DataServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.FileServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.UserServiceImpl;
import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;
import org.pf4j.DefaultPluginManager;
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
 * @author chengyuansen
 * @date 2025-12-18
 */
@Configuration
@ConditionalOnProperty(prefix = "onebase.plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PluginProperties.class)
public class PluginRuntimeAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PluginRuntimeAutoConfiguration.class);

    /**
     * 配置PF4J PluginManager
     *
     * @param properties         插件配置属性
     * @param applicationContext Spring应用上下文
     * @return PluginManager
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginManager pluginManager(PluginProperties properties, ApplicationContext applicationContext) {
        String pluginsDirStr = properties.getPluginsDir();
        
        // 处理 file: 前缀
        if (pluginsDirStr.startsWith("file:")) {
            pluginsDirStr = pluginsDirStr.substring(5); // 移除 "file:" 前缀
        }
        
        Path pluginsPath = Paths.get(pluginsDirStr);
        // 规范化路径，处理 .. 和 . 等相对符号
        Path normalizedPath = pluginsPath.normalize();
        Path absolutePath = normalizedPath.toAbsolutePath();
        String userDir = System.getProperty("user.dir");
        
        log.info("========== 插件管理器初始化信息 ==========");
        log.info("配置的插件目录（原始值）: {}", properties.getPluginsDir());
        log.info("当前工作目录: {}", userDir);
        log.info("规范化后的路径: {}", normalizedPath);
        log.info("解析后的绝对路径: {}", absolutePath);
        log.info("插件目录是否存在: {}", absolutePath.toFile().exists());
        log.info("插件目录是否为目录: {}", absolutePath.toFile().isDirectory());
        log.info("========================================");

        // 使用 DefaultPluginManager，不涉及Spring相关功能
        // 插件扩展点通过普通的ExtensionFactory创建，不需要Spring支持
        DefaultPluginManager pluginManager = new DefaultPluginManager(absolutePath) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                // 只使用 PropertiesPluginDescriptorFinder，读取 plugin.properties
                return new PropertiesPluginDescriptorFinder();
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

    /**
     * 配置插件HTTP分发器
     *
     * @param oneBasePluginManager         插件管理器
     * @param requestMappingHandlerAdapter Spring MVC 的请求处理适配器
     * @return 分发器
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginHttpDispatcher pluginHttpDispatcher(
            OneBasePluginManager oneBasePluginManager,
            org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        return new PluginHttpDispatcher(oneBasePluginManager, requestMappingHandlerAdapter);
    }

    /**
     * 配置插件HTTP处理器（代理控制器）
     *
     * @param pluginHttpDispatcher 分发器
     * @return 处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginHttpHandler pluginHttpHandler(PluginHttpDispatcher pluginHttpDispatcher) {
        return new PluginHttpHandler(pluginHttpDispatcher);
    }
}
