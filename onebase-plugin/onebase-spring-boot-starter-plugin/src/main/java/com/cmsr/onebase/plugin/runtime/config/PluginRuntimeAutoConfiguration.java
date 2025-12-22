package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.core.PluginMode;
import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpHandler;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpManager;
import com.cmsr.onebase.plugin.runtime.manager.DevModePluginManager;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.service.DataServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.FileServiceImpl;
import com.cmsr.onebase.plugin.runtime.service.UserServiceImpl;
import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
import com.cmsr.onebase.plugin.runtime.interceptor.PluginSecurityInterceptor;
import com.cmsr.onebase.plugin.runtime.listener.PluginLifecycleListener;

import jakarta.annotation.PreDestroy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件运行时自动配置
 * <p>
 * 配置PF4J的PluginManager和相关Bean。
 * 无论enabled是否为true，都会加载配置类和创建Bean，只是enabled=false时不执行主动初始化操作。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(PluginProperties.class)
public class PluginRuntimeAutoConfiguration {

    private final PluginProperties properties;

    public PluginRuntimeAutoConfiguration(PluginProperties properties) {
        // 强校验 mode 配置，若非法立即抛出异常，拒绝启动
        this.properties = properties;
        // 调用 getPluginMode() 会触发 PluginMode.fromValue 校验并抛出异常
        this.properties.getPluginMode();
    }

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
        // 检查插件系统是否启用
        if (!properties.isEnabled()) {
            log.info("插件系统已禁用（enabled=false）");
            // 创建一个空的PluginManager，不加载任何插件
            return new DefaultPluginManager() {
                @Override
                protected PluginDescriptorFinder createPluginDescriptorFinder() {
                    return new PropertiesPluginDescriptorFinder();
                }
            };
        }
        
        PluginMode mode = properties.getPluginMode(); // 会在此处验证模式值，无效则抛异常
        
        // DEV模式：只加载classpath下的扩展点
        if (mode.isDev()) {
            log.info("=".repeat(60));
            log.info("启用开发模式（mode=dev）");
            log.info("插件加载策略: 只扫描classpath中的扩展点实现类");
            log.info("支持的扩展点: DataProcessor, EventListener, HttpHandler");
            log.info("适用场景: IDE中直接启动和调试，支持断点调试");
            log.info("=".repeat(60));
            
            DevModePluginManager pluginManager = new DevModePluginManager(properties);
            
            // 根据autoLoad配置决定是否自动加载
            if (properties.isAutoLoad()) {
                pluginManager.loadPlugins();
                log.info("已自动加载开发模式插件");
            } else {
                log.info("跳过自动加载插件（autoLoad=false）");
            }
            
            // 根据autoStart配置决定是否自动启动
            if (properties.isAutoStart() && properties.isAutoLoad()) {
                pluginManager.startPlugins();
                log.info("已自动启动开发模式插件");
            } else {
                log.info("跳过自动启动插件（autoStart={} 或 autoLoad={}）", properties.isAutoStart(), properties.isAutoLoad());
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

            // 根据autoLoad配置决定是否自动加载
            if (properties.isAutoLoad()) {
                pluginManager.loadPlugins();
                log.info("已自动加载 {} 个插件", pluginManager.getPlugins().size());
            } else {
                log.info("跳过自动加载插件（autoLoad=false）");
            }
            
            // 根据autoStart配置决定是否自动启动
            if (properties.isAutoStart() && properties.isAutoLoad()) {
                pluginManager.startPlugins();
                log.info("已自动启动 {} 个插件", pluginManager.getStartedPlugins().size());
            } else {
                log.info("跳过自动启动插件（autoStart={} 或 autoLoad={}）", properties.isAutoStart(), properties.isAutoLoad());
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

        // 根据autoLoad配置决定是否自动加载
        if (properties.isAutoLoad()) {
            pluginManager.loadPlugins();
            log.info("已自动加载 {} 个插件", pluginManager.getPlugins().size());
        } else {
            log.info("跳过自动加载插件（autoLoad=false）");
        }
        
        // 根据autoStart配置决定是否自动启动
        if (properties.isAutoStart() && properties.isAutoLoad()) {
            pluginManager.startPlugins();
            log.info("已自动启动 {} 个插件", pluginManager.getStartedPlugins().size());
        } else {
            log.info("跳过自动启动插件（autoStart={} 或 autoLoad={}）", properties.isAutoStart(), properties.isAutoLoad());
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
    public OneBasePluginManager oneBasePluginManager(PluginManager pluginManager, PluginProperties properties,
                                                    org.springframework.context.ApplicationEventPublisher eventPublisher) {
        OneBasePluginManager oneBasePluginManager = new OneBasePluginManager(pluginManager, eventPublisher);
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
     * @param properties                   插件配置属性
     * @return 分发器
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginHttpDispatcher pluginHttpDispatcher(
            OneBasePluginManager oneBasePluginManager,
            org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            PluginProperties properties) {
        return new PluginHttpDispatcher(oneBasePluginManager, requestMappingHandlerAdapter, properties);
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
     * 插件安全拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginSecurityInterceptor pluginSecurityInterceptor(OneBasePluginManager oneBasePluginManager,
                                                               PluginProperties properties) {
        return new PluginSecurityInterceptor(oneBasePluginManager, properties);
    }

    /**
     * 插件HTTP管理器（统一HTTP处理模块，整合了原HttpHandlerRegistry、HttpRoutingManager、HttpHandlerInitializer）
     * <p>
     * 负责：
     * 1. 启动时发现并注册插件的HttpHandler
     * 2. 运行时注册/注销插件的HTTP处理器
     * 3. 管理路由元数据（pluginId -> handlers/mappings）
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    @org.springframework.context.annotation.Lazy
    public PluginHttpManager pluginHttpManager(
            org.springframework.beans.factory.ObjectProvider<OneBasePluginManager> oneBasePluginManagerProvider,
            org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping handlerMapping,
            PluginProperties pluginProperties) {
        return new PluginHttpManager(oneBasePluginManagerProvider, handlerMapping, pluginProperties);
    }

    /**
     * 插件生命周期监听器（负责在插件启动/停止/卸载时注册或注销路由）
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginLifecycleListener pluginLifecycleListener(OneBasePluginManager oneBasePluginManager,
                                                           PluginHttpDispatcher pluginHttpDispatcher,
                                                           PluginHttpManager pluginHttpManager) {
        return new PluginLifecycleListener(oneBasePluginManager, pluginHttpDispatcher, pluginHttpManager);
    }

}