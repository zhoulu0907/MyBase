package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.core.PluginMode;
import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.executor.EventDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginControllerRegistrar;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpManager;
import com.cmsr.onebase.plugin.runtime.manager.DevModePluginManager;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.reload.HotReloadConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.PropertiesPluginDescriptorFinder;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.cmsr.onebase.plugin.runtime.interceptor.PluginSecurityInterceptor;

import java.nio.file.Path;
import java.nio.file.Paths;
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
@Import(HotReloadConfiguration.class)
@EnableConfigurationProperties(PluginProperties.class)
public class PluginRuntimeAutoConfiguration {

    private final PluginProperties properties;
    private Path absolutePath;

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

        // 映射 onebase.plugin.mode 到 PF4J RuntimeMode
        // dev → development, staging/prod → deployment
        if (mode.isDev()) {
            System.setProperty("pf4j.mode", "development");
        } else {
            System.setProperty("pf4j.mode", "deployment");
        }

        initLogs(properties);// 打印启动日志

        // DEV模式：只加载classpath下的扩展点
        if (mode.isDev()) {
            // 创建 DEV 模式插件管理器
            DevModePluginManager pluginManager = new DevModePluginManager(properties, applicationContext);
            // 配置并初始化插件管理器
            return configureAndInitPluginManager(pluginManager);
        }

        // STAGING模式：只加载plugin目录ZIP包扩展点
        if (mode.isStaging()) {
            // 创建 STAGING 模式插件管理器
            SpringPluginManager pluginManager = createSpringPluginManager(this.absolutePath);
            // 配置并初始化插件管理器
            return configureAndInitPluginManager(pluginManager);
        }

        // PROD模式：使用SpringPluginManager加载plugin目录
        SpringPluginManager pluginManager = createSpringPluginManager(this.absolutePath);
        // 配置并初始化插件管理器
        return configureAndInitPluginManager(pluginManager);
    }

    private void initLogs(PluginProperties properties) {
        PluginMode mode = properties.getPluginMode();

        // 确定实际的 pf4j.mode 映射值
        String pf4jMode = mode.isDev() ? "development" : "deployment";

        // DEV模式配置的classpath
        List<String> devClassPaths = properties.getDevClassPaths();

        // STAGING和PROD模式都需要加载plugin目录
        String pluginsDirStr = properties.getPluginsDir();

        // 处理 file: 前缀
        if (pluginsDirStr.startsWith("file:")) {
            pluginsDirStr = pluginsDirStr.substring(5); // 移除 "file:" 前缀
        }

        Path pluginsPath = Paths.get(pluginsDirStr);
        // 规范化路径，处理 .. 和 . 等相对符号
        Path normalizedPath = pluginsPath.normalize();
        this.absolutePath = normalizedPath.toAbsolutePath();
        String userDir = System.getProperty("user.dir");

        // 构建加载策略描述
        String strategy;
        if (mode.isDev()) {
            strategy = devClassPaths.isEmpty()
                    ? "DEV 模式未配置 devClassPaths，不会加载任何扩展点（需配置 onebase.plugin.dev-class-paths）"
                    : String.format("扫描配置目录中的扩展点实现类 %s", String.join(", ", devClassPaths));
        } else {
            strategy = String.format("加载插件目录 %s 中的 ZIP/JAR 插件包", this.absolutePath);
        }

        log.info("启用模式: {}", mode.getValue());
        log.info("映射运行模式: onebase.plugin.mode={} → pf4j.mode={}", mode.getValue(), pf4jMode);

        // DEV 模式下如果 devClassPaths 为空，使用 WARN 级别提醒用户
        if (mode.isDev() && devClassPaths.isEmpty()) {
            log.warn("插件加载策略: {}", strategy);
        } else {
            log.info("插件加载策略: {}", strategy);
        }

        // 只在非 DEV 模式下打印插件目录详细信息
        if (!mode.isDev()) {
            log.info("配置的插件目录（原始值）: {}", pluginsDirStr);
            log.info("当前工作目录: {}", userDir);
            log.info("规范化后的路径: {}", normalizedPath);
            log.info("解析后的绝对路径: {}", this.absolutePath);
            log.info("插件目录是否存在: {}", this.absolutePath.toFile().exists());
            log.info("插件目录是否为目录: {}", this.absolutePath.toFile().isDirectory());
        }
    }

    /**
     * 创建基础的 SpringPluginManager 实例
     *
     * @param pluginsPath 插件目录路径
     * @return SpringPluginManager 实例
     */
    private SpringPluginManager createSpringPluginManager(Path pluginsPath) {
        return new SpringPluginManager(pluginsPath) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new PropertiesPluginDescriptorFinder();
            }

            @Override
            public void init() {
                // 覆盖父类 init 方法，防止自动调用 loadPlugins/startPlugins
                log.debug("覆盖 SpringPluginManager.init()，跳过默认的自动加载/启动");
            }
        };
    }

    /**
     * 配置并初始化插件管理器
     * 此方法被 DEV/STAGING/PROD 三种模式共用
     *
     * @param pluginManager SpringPluginManager 实例（可以是 DevModePluginManager）
     * @return 配置好的插件管理器
     */
    private SpringPluginManager configureAndInitPluginManager(SpringPluginManager pluginManager) {
        // 根据 autoLoad 配置决定是否加载插件
        if (properties.isAutoLoad()) {
            pluginManager.loadPlugins();

            // DEV 模式和 STAGING/PROD 模式使用不同的日志描述
            if (properties.isDevMode()) {
                log.info("已创建开发模式虚拟插件（用于 classpath 扩展点扫描）");
            } else {
                int loadedCount = pluginManager.getPlugins().size();
                log.info("已加载 {} 个插件", loadedCount);
            }

            // 根据 autoStart 配置决定是否启动插件
            if (properties.isAutoStart()) {
                pluginManager.startPlugins();

                if (properties.isDevMode()) {
                    log.info("已启动开发模式虚拟插件");
                } else {
                    log.info("已启动 {} 个插件", pluginManager.getStartedPlugins().size());
                }
            } else {
                log.info("插件已加载但未启动（autoStart=false）");
            }
        } else {
            log.info("插件管理器已就绪（autoLoad=false，插件未加载）");
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
            ApplicationEventPublisher eventPublisher, ObjectProvider<PluginControllerRegistrar> registrarProvider) {
        OneBasePluginManager manager = new OneBasePluginManager(pluginManager, eventPublisher);

        // 设置 Controller 注册器
        registrarProvider.ifAvailable(registrar -> {
            manager.setControllerRegistrar(registrar);

            // 为已启动的插件注册 Controller
            // （因为 pluginManager.startPlugins() 在此之前已经调用过了）
            for (PluginWrapper plugin : pluginManager.getStartedPlugins()) {
                String pluginId = plugin.getPluginId();
                List<HttpHandler> handlers = manager.getHttpHandlers(pluginId);
                if (handlers != null && !handlers.isEmpty()) {
                    log.info("为已启动的插件 {} 注册 {} 个 Controller", pluginId, handlers.size());
                    registrar.registerControllers(pluginId, handlers);
                }
            }
        });

        return manager;
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
     * @param pluginManager  插件管理器
     * @param contextFactory 上下文工厂
     * @return EventDispatcher
     */
    @Bean
    @ConditionalOnMissingBean
    public EventDispatcher eventDispatcher(OneBasePluginManager pluginManager, PluginContextFactory contextFactory) {
        return new EventDispatcher(pluginManager, contextFactory);
    }

    /**
     * 插件HTTP管理器（简化版，仅用于查询）
     */
    @Bean
    @ConditionalOnMissingBean
    @Lazy
    public PluginHttpManager pluginHttpManager(ObjectProvider<OneBasePluginManager> oneBasePluginManagerProvider) {
        return new PluginHttpManager(oneBasePluginManagerProvider);
    }

    /**
     * 插件Controller注册器
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginControllerRegistrar pluginControllerRegistrar(RequestMappingHandlerMapping handlerMapping) {
        return new PluginControllerRegistrar(handlerMapping);
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
}