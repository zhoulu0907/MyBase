package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.core.PluginMode;
import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.DevModePluginManager;
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

import jakarta.annotation.PreDestroy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        PluginMode mode = properties.getPluginMode(); // 会在此处验证模式值，无效则抛异常
        
        // DEV模式：只加载classpath下的扩展点
        if (mode.isDev()) {
            log.info("=".repeat(60));
            log.info("启用开发模式（mode=dev）");
            log.info("插件加载策略: 只扫描classpath中的扩展点实现类");
            log.info("支持的扩展点: DataProcessor, EventListener, HttpHandler");
            log.info("适用场景: IDE中直接启动和调试，支持断点调试");
            log.info("=".repeat(60));
            
            DevModePluginManager pluginManager = new DevModePluginManager();
            
            if (properties.isAutoLoad()) {
                pluginManager.loadPlugins();
                
                if (properties.isAutoStart()) {
                    pluginManager.startPlugins();
                }
            }
            
            return pluginManager;
        }
        
        // STAGING和PROD模式都需要加载plugin目录
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
        
        // STAGING模式：只加载plugin目录ZIP包扩展点
        if (mode.isStaging()) {
            log.info("=".repeat(60));
            log.info("启用预发布模式（mode=staging）");
            log.info("插件加载策略: 只从plugin目录加载ZIP/JAR插件，不扫描classpath");
            log.info("适用场景: 验证插件完整生命周期、测试ClassLoader隔离");
            log.info("=".repeat(60));
            
            log.info("========== 插件管理器初始化信息 ==========");
            log.info("配置的插件目录（原始值）: {}", properties.getPluginsDir());
            log.info("当前工作目录: {}", userDir);
            log.info("规范化后的路径: {}", normalizedPath);
            log.info("解析后的绝对路径: {}", absolutePath);
            log.info("插件目录是否存在: {}", absolutePath.toFile().exists());
            log.info("插件目录是否为目录: {}", absolutePath.toFile().isDirectory());
            log.info("========================================");

            DefaultPluginManager pluginManager = new DefaultPluginManager(absolutePath) {
                @Override
                protected PluginDescriptorFinder createPluginDescriptorFinder() {
                    return new PropertiesPluginDescriptorFinder();
                }
                
                @Override
                public <T> List<T> getExtensions(Class<T> type) {
                    // Staging模式：只从已启动的插件中获取扩展点，不扫描classpath
                    List<T> extensions = new ArrayList<>();
                    for (org.pf4j.PluginWrapper plugin : getStartedPlugins()) {
                        extensions.addAll(getExtensions(type, plugin.getPluginId()));
                    }
                    log.debug("Staging模式：从 {} 个插件中获取 {} 个 {} 扩展点", 
                             getStartedPlugins().size(), extensions.size(), type.getSimpleName());
                    return extensions;
                }
            };

            if (properties.isAutoLoad()) {
                pluginManager.loadPlugins();
                log.info("已加载 {} 个插件", pluginManager.getPlugins().size());

                if (properties.isAutoStart()) {
                    pluginManager.startPlugins();
                    log.info("已启动 {} 个插件", pluginManager.getStartedPlugins().size());
                }
            }

            return pluginManager;
        }
        
        // PROD模式：使用PF4J默认策略（classpath + plugin目录都扫描）
        log.info("=".repeat(60));
        log.info("启用生产模式（mode=prod）");
        log.info("插件加载策略: 使用PF4J默认策略（classpath + plugin目录都扫描）");
        log.info("适用场景: 生产环境，提供最大灵活性");
        log.info("=".repeat(60));
        
        log.info("========== 插件管理器初始化信息 ==========");
        log.info("配置的插件目录（原始值）: {}", properties.getPluginsDir());
        log.info("当前工作目录: {}", userDir);
        log.info("规范化后的路径: {}", normalizedPath);
        log.info("解析后的绝对路径: {}", absolutePath);
        log.info("插件目录是否存在: {}", absolutePath.toFile().exists());
        log.info("插件目录是否为目录: {}", absolutePath.toFile().isDirectory());
        log.info("========================================");

        // 使用原生DefaultPluginManager，不覆写getExtensions()
        DefaultPluginManager pluginManager = new DefaultPluginManager(absolutePath) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new PropertiesPluginDescriptorFinder();
            }
        };

        if (properties.isAutoLoad()) {
            pluginManager.loadPlugins();
            log.info("已加载 {} 个插件", pluginManager.getPlugins().size());

            if (properties.isAutoStart()) {
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
     * @param properties    插件配置属性
     * @return OneBasePluginManager
     */
    @Bean
    @ConditionalOnMissingBean
    public OneBasePluginManager oneBasePluginManager(PluginManager pluginManager, PluginProperties properties) {
        OneBasePluginManager oneBasePluginManager = new OneBasePluginManager(pluginManager);
        
        return oneBasePluginManager;
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

    /**
     * 容器销毁时停止插件目录扫描器
     */
    @PreDestroy
    public void destroy() {
        // 无需处理扫描器停止，相关组件若存在应自行管理生命周期
    }
}
