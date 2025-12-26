package com.cmsr.onebase.plugin.runtime.reload;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.http.PluginControllerRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热重载管理器
 * <p>
 * 管理插件扩展点的热重载生命周期：
 * 1. 卸载旧的 Spring Bean 和 HTTP 路由
 * 2. 创建新的 ClassLoader 加载更新后的类
 * 3. 注册新的 Spring Bean 和 HTTP 路由
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-23
 */
@Slf4j
public class HotReloadManager {

    private final ApplicationContext applicationContext;
    private final PluginControllerRegistrar controllerRegistrar;
    private final Path classesRoot;

    /**
     * 记录每个类对应的 ClassLoader
     * 用于热重载时创建新的 ClassLoader
     */
    private final Map<String, URLClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    /**
     * 记录每个类对应的 Bean 名称
     */
    private final Map<String, String> beanNameMap = new ConcurrentHashMap<>();

    public HotReloadManager(ApplicationContext applicationContext,
            PluginControllerRegistrar controllerRegistrar,
            Path classesRoot) {
        this.applicationContext = applicationContext;
        this.controllerRegistrar = controllerRegistrar;
        this.classesRoot = classesRoot;
    }

    /**
     * 重载扩展点
     *
     * @param className 完整类名
     * @param classFile .class 文件路径
     */
    public synchronized void reloadExtension(String className, Path classFile) {
        log.info("开始热重载扩展点: {}", className);

        try {
            // 1. 卸载旧的扩展点
            unloadExtension(className);

            // 2. 创建新的 ClassLoader 加载更新后的类
            URLClassLoader newClassLoader = createClassLoader(classesRoot);
            Class<?> newClass = newClassLoader.loadClass(className);

            // 3. 检查是否是 HttpHandler
            if (HttpHandler.class.isAssignableFrom(newClass)) {
                // 4. 注册为 Spring Bean
                String beanName = registerSpringBean(newClass, className);
                beanNameMap.put(className, beanName);

                // 5. 注册 HTTP 路由（使用新的 PluginControllerRegistrar）
                ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
                        .getBeanFactory();
                Object handlerInstance = beanFactory.getBean(beanName);

                // 将单个 handler 包装为 List 并注册
                @SuppressWarnings("unchecked")
                HttpHandler handler = (HttpHandler) handlerInstance;
                controllerRegistrar.registerControllers("dev-mode-plugin", Collections.singletonList(handler));

                log.info("热重载成功: {} (Bean: {})", className, beanName);
            } else {
                log.warn("类 {} 不是 HttpHandler，跳过热重载", className);
            }

            // 6. 记录新的 ClassLoader
            classLoaderMap.put(className, newClassLoader);

        } catch (Exception e) {
            log.error("热重载失败: {}", className, e);
        }
    }

    /**
     * 卸载扩展点
     *
     * @param className 完整类名
     */
    public synchronized void unloadExtension(String className) {
        log.debug("卸载扩展点: {}", className);

        try {
            // 1. 卸载 HTTP 路由（使用新的 PluginControllerRegistrar）
            controllerRegistrar.unregisterControllerByClassName(className);

            // 2. 卸载 Spring Bean
            String beanName = beanNameMap.get(className);
            if (beanName != null) {
                ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
                        .getBeanFactory();
                if (beanFactory instanceof DefaultListableBeanFactory) {
                    ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition(beanName);
                    log.debug("已移除 Spring Bean: {}", beanName);
                }
                beanNameMap.remove(className);
            }

            // 3. 关闭旧的 ClassLoader
            URLClassLoader oldClassLoader = classLoaderMap.remove(className);
            if (oldClassLoader != null) {
                try {
                    oldClassLoader.close();
                    log.debug("已关闭旧的 ClassLoader: {}", className);
                } catch (Exception e) {
                    log.warn("关闭 ClassLoader 失败: {}", className, e);
                }
            }

        } catch (Exception e) {
            log.error("卸载扩展点失败: {}", className, e);
        }
    }

    /**
     * 创建新的 ClassLoader
     * <p>
     * 关键修复：使用父类加载器优先加载 com.cmsr.onebase.plugin.api 包下的类，
     * 避免 ClassLoader 隔离导致的类型检查失败。
     * </p>
     */
    private URLClassLoader createClassLoader(Path classesRoot) throws Exception {
        URL[] urls = new URL[] { classesRoot.toUri().toURL() };

        // 使用自定义 ClassLoader，对 plugin API 包使用父类加载器优先
        return new URLClassLoader(urls, getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                // 对于插件 API 包（HttpHandler 等接口），强制使用父类加载器
                if (name.startsWith("com.cmsr.onebase.plugin.api.")) {
                    return getParent().loadClass(name);
                }
                // 其他类使用默认的双亲委派机制
                return super.loadClass(name);
            }
        };
    }

    /**
     * 注册 Spring Bean
     */
    private String registerSpringBean(Class<?> clazz, String className) {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctx.getBeanFactory();

        // 使用时间戳确保 Bean 名称唯一
        String beanName = className + "_hotReload_" + System.currentTimeMillis();

        GenericBeanDefinition beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(clazz);
        beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        beanFactory.registerBeanDefinition(beanName, beanDef);
        log.debug("已注册 Spring Bean: {} -> {}", className, beanName);

        return beanName;
    }

    /**
     * 清理所有资源
     */
    public void shutdown() {
        log.info("关闭热重载管理器");

        // 关闭所有 ClassLoader
        for (URLClassLoader classLoader : classLoaderMap.values()) {
            try {
                classLoader.close();
            } catch (Exception e) {
                log.warn("关闭 ClassLoader 失败", e);
            }
        }

        classLoaderMap.clear();
        beanNameMap.clear();
    }
}
