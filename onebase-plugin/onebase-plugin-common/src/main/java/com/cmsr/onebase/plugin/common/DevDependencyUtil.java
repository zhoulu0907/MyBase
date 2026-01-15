package com.cmsr.onebase.plugin.common;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发模式工具类
 * <p>
 * 提供开发模式下通用的类路径和依赖加载逻辑
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-14
 */
@Slf4j
public class DevDependencyUtil {

    private static final String DEV_DEPENDENCIES_FILE = "dev-dependencies-classpath.txt";

    /**
     * 获取指定类路径目录及其关联的第三方依赖的 URL 列表
     *
     * @param classRoot 插件编译输出目录 (classes root)
     * @return 包含 classRoot 和所有依赖 jar 的 URL 列表
     */
    public static List<URL> getUrlsWithDependencies(Path classRoot) {
        List<URL> urls = new ArrayList<>();
        try {
            // 1. 添加自身的路径
            urls.add(classRoot.toUri().toURL());
            log.debug("加入开发模式类路径: {}", classRoot);

            // 2. 检查并加载 dev-dependencies-classpath.txt
            Path devClasspathFile = classRoot.resolve(DEV_DEPENDENCIES_FILE);
            if (Files.exists(devClasspathFile)) {
                log.info("发现开发依赖类路径文件: {}", devClasspathFile);
                try {
                    List<String> dependencyPaths = Files.readAllLines(devClasspathFile);
                    int loadedCount = 0;
                    for (String depPath : dependencyPaths) {
                        String trimmedPath = depPath.trim();
                        if (!trimmedPath.isEmpty()) {
                            Path dependencyPath = Paths.get(trimmedPath);
                            if (Files.exists(dependencyPath)) {
                                urls.add(dependencyPath.toUri().toURL());
                                loadedCount++;
                                log.debug("  加载依赖: {}", dependencyPath.getFileName());
                            } else {
                                log.warn("依赖文件不存在: {}", trimmedPath);
                            }
                        }
                    }
                    log.info("从 {} 加载了 {} 个第三方依赖", devClasspathFile.getFileName(), loadedCount);
                } catch (Exception e) {
                    log.error("读取开发依赖类路径文件失败: {}", devClasspathFile, e);
                }
            }
        } catch (Exception e) {
            log.error("处理开发模式类路径失败: {}", classRoot, e);
        }
        return urls;
    }
}
