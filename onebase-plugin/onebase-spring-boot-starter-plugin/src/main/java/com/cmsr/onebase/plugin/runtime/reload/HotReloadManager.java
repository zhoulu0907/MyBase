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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.cmsr.onebase.plugin.common.DevDependencyUtil;

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
    private final List<Path> classesRoots; // 已去重的插件路径

    /**
     * 记录每个类对应的 ClassLoader
     * 用于热重载时创建新的 ClassLoader
     */
    private final Map<String, URLClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    /**
     * 记录每个类对应的 Bean 名称
     */
    private final Map<String, String> beanNameMap = new ConcurrentHashMap<>();

    /**
     * 记录每个插件包含的扩展点
     * 插件 classes 目录 → 该插件的所有扩展点类名
     * 用于插件级热重载
     */
    private final Map<Path, Set<String>> pluginExtensionsMap = new ConcurrentHashMap<>();

    public HotReloadManager(ApplicationContext applicationContext,
            PluginControllerRegistrar controllerRegistrar,
            List<Path> classesRoots) {
        this.applicationContext = applicationContext;
        this.controllerRegistrar = controllerRegistrar;

        // 去重并规范化插件路径
        this.classesRoots = classesRoots.stream()
                .map(Path::toAbsolutePath)
                .map(Path::normalize)
                .distinct()
                .collect(Collectors.toList());

        log.info("初始化 HotReloadManager，监听 {} 个插件路径", this.classesRoots.size());
        this.classesRoots.forEach(p -> log.debug("  - {}", p));
    }

    /**
     * 注册扩展点到插件映射
     * 在扫描时调用，建立插件与扩展点的关联
     *
     * @param className       扩展点类名
     * @param pluginClassPath 插件的 classes 目录
     */
    public void registerExtension(String className, Path pluginClassPath) {
        // 【P0 修复】：参数校验
        if (className == null || className.trim().isEmpty()) {
            log.warn("注册扩展点失败：类名为空");
            return;
        }
        if (pluginClassPath == null) {
            log.warn("注册扩展点失败：插件路径为空，类名: {}", className);
            return;
        }

        // 【P0 修复】：路径规范化，确保与 classesRoots 一致
        Path normalizedPath = pluginClassPath.toAbsolutePath().normalize();

        pluginExtensionsMap
                .computeIfAbsent(normalizedPath, k -> ConcurrentHashMap.newKeySet())
                .add(className);

        log.info("注册扩展点: {} → 插件: {}", className, normalizedPath.getFileName());
    }

    /**
     * 根据类文件路径找到所属插件
     *
     * @param classFile 类文件的完整路径
     * @return 插件的 classes 目录，如果找不到返回 null
     */
    public Path findPluginForClassFile(Path classFile) {
        if (classFile == null) {
            log.warn("查找插件失败：类文件路径为空");
            return null;
        }

        // 【P0 修复】：路径规范化，避免匹配错误
        Path normalizedClassFile = classFile.toAbsolutePath().normalize();

        // 【P0 修复】：确保是真正的子路径，而不是前缀匹配
        // 例如：避免 plugin-a-ext 被错误匹配到 plugin-a
        for (Path pluginClassPath : classesRoots) {
            if (normalizedClassFile.startsWith(pluginClassPath) &&
                    !normalizedClassFile.equals(pluginClassPath)) {
                // 额外验证：确保是目录边界
                // 例如：D:/plugin-a/target/classes/Foo.class 匹配 D:/plugin-a/target/classes
                // 但 D:/plugin-a-ext/target/classes/Foo.class 不匹配 D:/plugin-a/target/classes
                try {
                    // relativize 用于验证是否是真正的子路径，变量本身不需要使用
                    @SuppressWarnings("unused")
                    Path relative = pluginClassPath.relativize(normalizedClassFile);
                    // 如果能成功 relativize，说明是真正的子路径
                    return pluginClassPath;
                } catch (IllegalArgumentException e) {
                    // 不是子路径，继续查找
                    continue;
                }
            }
        }

        log.warn("无法确定类文件 {} 属于哪个插件", classFile);
        return null;
    }

    /**
     * 重载整个插件的所有扩展点
     *
     * @param pluginClassPath 插件的 classes 目录
     */
    public synchronized void reloadPlugin(Path pluginClassPath) {
        Set<String> extensions = pluginExtensionsMap.get(pluginClassPath);

        if (extensions == null || extensions.isEmpty()) {
            log.warn("插件 {} 没有扩展点，跳过重载", pluginClassPath.getFileName());
            return;
        }

        log.info("重载插件 {}，共 {} 个扩展点",
                pluginClassPath.getFileName(), extensions.size());

        for (String className : extensions) {
            try {
                reloadExtension(className, null);
            } catch (Exception e) {
                log.error("重载扩展点失败: {}", className, e);
            }
        }

        log.info("插件 {} 重载完成", pluginClassPath.getFileName());
    }

    /**
     * 热重载扩展点
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
        log.info("卸载扩展点: {}", className);

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
                    log.info("已移除 Spring Bean: {}", beanName);
                }
                beanNameMap.remove(className);
            }

            // 3. 关闭旧的 ClassLoader
            URLClassLoader oldClassLoader = classLoaderMap.remove(className);
            if (oldClassLoader != null) {
                try {
                    oldClassLoader.close();
                    log.info("已关闭旧的 ClassLoader: {}", className);
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
        List<URL> allUrls = new ArrayList<>();

        // 1. 添加插件类目录及其依赖
        for (Path classRoot : classesRoots) {
            try {
                // 使用工具类加载类路径和依赖
                allUrls.addAll(DevDependencyUtil.getUrlsWithDependencies(classRoot));
            } catch (Exception e) {
                log.warn("热重载: 处理类路径失败: {}", classRoot, e);
            }
        }

        // 2. 转换为 URL 数组
        URL[] urls = allUrls.toArray(new URL[0]);
        log.info("创建热重载 ClassLoader，包含 {} 个路径", urls.length);

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
