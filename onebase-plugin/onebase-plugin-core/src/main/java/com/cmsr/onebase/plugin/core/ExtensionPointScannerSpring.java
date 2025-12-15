package com.cmsr.onebase.plugin.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 扩展点扫描器（Spring实现）
 * <p>
 * 使用Spring的ClassPathScanningCandidateComponentProvider在运行时扫描classpath，
 * 查找所有扩展点接口的实现类，用于开发模式热加载。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-15
 */
public class ExtensionPointScannerSpring {

    private static final Logger log = LoggerFactory.getLogger(ExtensionPointScannerSpring.class);

    /**
     * 扫描指定类型的扩展点实现类
     *
     * @param extensionType 扩展点接口类型
     * @param <T> 扩展点类型
     * @return 扩展点实例列表
     */
    public <T> List<T> scanExtensions(Class<T> extensionType) {
        log.debug("开始扫描扩展点: {}", extensionType.getName());

        List<T> extensions = new ArrayList<>();

        try {
            // 创建Spring classpath扫描器
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);

            // 添加类型过滤器
            scanner.addIncludeFilter(new AssignableTypeFilter(extensionType));

            // 扫描整个classpath（空字符串表示扫描所有包）
            Set<BeanDefinition> candidates = scanner.findCandidateComponents("");

            log.debug("发现 {} 个候选类", candidates.size());

            for (BeanDefinition bd : candidates) {
                String className = bd.getBeanClassName();
                try {
                    Class<?> clazz = Class.forName(className);

                    // 跳过抽象类和接口
                    if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                        log.debug("跳过抽象类/接口: {}", className);
                        continue;
                    }

                    // 确保类实现了目标接口
                    if (!extensionType.isAssignableFrom(clazz)) {
                        log.debug("类 {} 未实现接口 {}", className, extensionType.getName());
                        continue;
                    }

                    // 实例化扩展点
                    @SuppressWarnings("unchecked")
                    T instance = (T) clazz.getDeclaredConstructor().newInstance();
                    extensions.add(instance);
                    log.info("发现扩展点: {} -> {}", extensionType.getSimpleName(), className);

                } catch (Exception e) {
                    log.warn("实例化扩展点失败 {}: {}", className, e.getMessage());
                }
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
