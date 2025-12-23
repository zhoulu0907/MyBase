package com.cmsr.onebase.plugin.core;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 扩展点扫描器（Spring实现）
 * <p>
 * 使用Spring的ClassPathScanningCandidateComponentProvider在运行时扫描classpath，
 * 查找所有扩展点接口的实现类，用于开发模式热加载。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-15
 */
@Slf4j
public class ExtensionPointScannerSpring {

    /**
     * 扫描指定类型的扩展点实现类
     *
     * @param extensionType 扩展点接口类型
     * @param <T>           扩展点类型
     * @return 扩展点实例列表
     */
    private final List<String> devClassPaths;
    private final ApplicationContext applicationContext;

    public ExtensionPointScannerSpring() {
        this.devClassPaths = Collections.emptyList();
        this.applicationContext = null;
    }

    /**
     * 构造器，传入开发模式下的类路径目录列表（绝对路径）和 Spring 应用上下文
     * 
     * @param devClassPaths      目录列表，仅从目录加载类文件，不从jar/zip加载
     * @param applicationContext Spring 应用上下文，用于将扫描到的类注册为 Spring Bean
     */
    public ExtensionPointScannerSpring(List<String> devClassPaths, ApplicationContext applicationContext) {
        this.devClassPaths = devClassPaths == null ? Collections.emptyList() : new ArrayList<>(devClassPaths);
        this.applicationContext = applicationContext;
    }

    public <T> List<T> scanExtensions(Class<T> extensionType) {
        log.debug("开始扫描扩展点: {}", extensionType.getName());

        List<T> extensions = new ArrayList<>();

        try {
            // 当在开发模式下使用外部指定类目录时，如果未指定任何 devClassPaths，直接返回空并记录告警
            if (devClassPaths == null || devClassPaths.isEmpty()) {
                log.warn("未指定devClassPaths，无法扫描扩展点。{}");
                return extensions;
            }

            // 仅从指定目录加载类（不加载jar/zip）
            List<URL> urls = new ArrayList<>();
            for (String p : devClassPaths) {
                if (p == null || p.trim().isEmpty())
                    continue;
                Path path = Paths.get(p.trim());
                if (!Files.exists(path) || !Files.isDirectory(path)) {
                    log.error("开发模式类路径不存在或不是目录: {}", path);
                    continue;
                }
                try {
                    urls.add(path.toUri().toURL());
                    log.debug("加入开发模式类路径: {}", path.toString());
                } catch (Exception e) {
                    log.warn("转换类路径为URL失败 {}: {}", p, e.getMessage());
                }
            }

            if (urls.isEmpty()) {
                log.warn("未找到有效的开发模式类路径");
                return extensions;
            }
            URL[] urlArray = urls.toArray(new URL[0]);
            try (URLClassLoader urlClassLoader = new URLClassLoader(urlArray,
                    Thread.currentThread().getContextClassLoader())) {
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory();

                // 遍历目录下的 class 文件，按包名方式扫描
                for (String dir : devClassPaths) {
                    Path base = Paths.get(dir);
                    if (!Files.exists(base) || !Files.isDirectory(base))
                        continue;
                    Files.walk(base)
                            .filter(p -> p.toString().endsWith(".class"))
                            .forEach(classFile -> {
                                try {
                                    String relative = base.relativize(classFile).toString();
                                    String className = relative.replaceAll("\\\\", "/")
                                            .replace('/', '.')
                                            .replaceAll("\\.class$", "");
                                    Class<?> clazz = Class.forName(className, false, urlClassLoader);
                                    if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface())
                                        return;
                                    if (!extensionType.isAssignableFrom(clazz))
                                        return;

                                    // 如果有 ApplicationContext，注册为 Spring Bean；否则使用反射创建实例
                                    T instance;
                                    if (applicationContext != null) {
                                        instance = registerAsSpringBean(clazz, extensionType);
                                        log.debug("发现扩展点并注册为 Spring Bean: {} -> {} (来自 {})",
                                                extensionType.getSimpleName(), className, base);
                                    } else {
                                        @SuppressWarnings("unchecked")
                                        T plainInstance = (T) clazz.getDeclaredConstructor().newInstance();
                                        instance = plainInstance;
                                        log.debug("发现扩展点: {} -> {} (来自 {})", extensionType.getSimpleName(), className,
                                                base);
                                    }
                                    extensions.add(instance);
                                } catch (ClassNotFoundException cnf) {
                                    log.debug("类未找到: {}", cnf.getMessage());
                                } catch (NoClassDefFoundError nde) {
                                    log.debug("类依赖缺失: {}", nde.getMessage());
                                } catch (Exception e) {
                                    log.warn("从开发目录加载扩展点失败 {}: {}", classFile, e.getMessage());
                                }
                            });
                }
            } catch (Exception e) {
                log.error("使用 URLClassLoader 加载开发类路径失败", e);
            }

            if (extensions.isEmpty()) {
                log.warn("未在classpath中找到 {} 的扩展点实现", extensionType.getName());
            }

        } catch (Exception e) {
            log.error("扫描扩展点失败", e);
        }

        log.debug("共发现 {} 个 {} 扩展点", extensions.size(), extensionType.getSimpleName());
        return extensions;
    }

    /**
     * 将扫描到的类注册为 Spring Bean
     * 
     * @param clazz         要注册的类
     * @param extensionType 扩展点接口类型
     * @param <T>           扩展点类型
     * @return Spring 管理的 Bean 实例
     */
    private <T> T registerAsSpringBean(Class<?> clazz, Class<T> extensionType) {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctx.getBeanFactory();

        String beanName = clazz.getName() + "_devPlugin";

        // 检查是否已注册
        if (!beanFactory.containsBean(beanName)) {
            GenericBeanDefinition beanDef = new GenericBeanDefinition();
            beanDef.setBeanClass(clazz);
            beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            beanFactory.registerBeanDefinition(beanName, beanDef);
            log.debug("已将 {} 注册为 Spring Bean: {}", clazz.getName(), beanName);
        }

        // 返回 Spring 管理的实例
        return extensionType.cast(beanFactory.getBean(beanName));
    }

    /**
     * 扫描所有已知扩展点接口的实现类
     *
     * @return Map<扩展点接口, 实现类列表>
     */
    public Map<Class<?>, List<Object>> scanAllExtensions() {
        Map<Class<?>, List<Object>> result = new HashMap<>();

        for (Class<?> extensionType : ExtensionPointConstants.EXTENSION_POINT_CLASSES) {
            @SuppressWarnings("unchecked")
            List<Object> extensions = (List<Object>) scanExtensions(extensionType);
            result.put(extensionType, extensions);
        }

        return result;
    }
}
