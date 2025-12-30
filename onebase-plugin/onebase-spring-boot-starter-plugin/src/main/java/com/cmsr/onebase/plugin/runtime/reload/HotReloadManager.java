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
import java.util.List;
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
    private final List<Path> classesRoots;

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
            List<Path> classesRoots) {
        this.applicationContext = applicationContext;
        this.controllerRegistrar = controllerRegistrar;
        this.classesRoots = classesRoots;
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
            URLClassLoader newClassLoader = createClassLoader();
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
     * 使用 Child-First（子类优先）策略加载插件类，确保热重载时能加载到新的类文件。
     * 策略：只有在 URL 中能找到的类才使用 Child-First，其他所有类都委托给父加载器。
     * </p>
     */
    private URLClassLoader createClassLoader() throws Exception {
        // 将所有 classesRoots 转换为 URL 数组
        URL[] urls = classesRoots.stream()
                .map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to convert path to URL: " + path, e);
                    }
                })
                .toArray(URL[]::new);

        log.debug("创建 ClassLoader，包含 {} 个路径: {}", urls.length, classesRoots);

        // Child-First ClassLoader：只对能在 URL 中找到的类使用子加载器
        return new URLClassLoader(urls, getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                synchronized (getClassLoadingLock(name)) {
                    // 1. 检查是否已加载
                    Class<?> loadedClass = findLoadedClass(name);
                    if (loadedClass != null) {
                        return loadedClass;
                    }

                    // 2. 插件 API 接口必须由父加载器加载，否则 instanceof 检查会失败
                    if (name.startsWith("com.cmsr.onebase.plugin.api.")) {
                        return getParent().loadClass(name);
                    }

                    // 3. Child-First：先尝试在自己的 URL 中查找
                    // findClass() 只在 URL（target/classes）中查找，
                    // 如果找不到会抛出 ClassNotFoundException
                    try {
                        return findClass(name);
                    } catch (ClassNotFoundException e) {
                        // 4. 自己找不到，委托给父加载器
                        return getParent().loadClass(name);
                    }
                }
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
