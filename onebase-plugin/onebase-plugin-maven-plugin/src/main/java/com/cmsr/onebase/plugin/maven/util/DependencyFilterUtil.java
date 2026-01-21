package com.cmsr.onebase.plugin.maven.util;

import com.cmsr.onebase.plugin.maven.constant.PluginPackagingConstants;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 依赖过滤工具类
 * <p>
 * 提供统一的依赖筛选逻辑，确保 PackagePluginMojo 和 GenerateDevClasspathMojo 使用相同的策略。
 * </p>
 * <p>
 * 依赖筛选规则：
 * <ul>
 * <li>只包含 scope 为 compile 或 runtime 的直接依赖</li>
 * <li>排除 scope 为 provided、test、system 的依赖（及其传递依赖）</li>
 * <li>排除宿主核心模块（定义在 PluginPackagingConstants.HOST_PROVIDED_ARTIFACTS）</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-15
 */
public class DependencyFilterUtil {

    /**
     * 收集插件的运行时依赖
     * <p>
     * 策略：只收集项目的直接依赖（不包括传递依赖），且 scope 为 compile 或 runtime。
     * 这样可以完全避免 provided 依赖的传递依赖被包含进来。
     * </p>
     *
     * @param project Maven 项目
     * @param log     Maven 日志
     * @return 依赖的 Artifact 列表
     */
    public static List<Artifact> collectRuntimeDependencies(MavenProject project, Log log) {
        List<Artifact> result = new ArrayList<>();

        // 只处理直接依赖
        for (org.apache.maven.model.Dependency dep : project.getDependencies()) {
            String scope = dep.getScope();

            // 默认 scope 是 compile
            if (scope == null) {
                scope = "compile";
            }

            // 只包含 compile 和 runtime scope
            if (!"compile".equals(scope) && !"runtime".equals(scope)) {
                log.debug("排除非 compile/runtime scope 的直接依赖: " + dep.getArtifactId() + " (scope: " + scope + ")");
                continue;
            }

            // 排除宿主核心模块
            if (PluginPackagingConstants.HOST_PROVIDED_ARTIFACTS.contains(dep.getArtifactId())) {
                log.debug("排除宿主核心模块: " + dep.getArtifactId());
                continue;
            }

            // 从已解析的 artifacts 中找到对应的 artifact
            for (Artifact artifact : project.getArtifacts()) {
                if (artifact.getGroupId().equals(dep.getGroupId())
                        && artifact.getArtifactId().equals(dep.getArtifactId())) {
                    File artifactFile = artifact.getFile();
                    if (artifactFile != null && artifactFile.exists()) {
                        result.add(artifact);
                        log.debug("  包含直接依赖: " + artifact.getArtifactId() + " -> " + artifactFile.getAbsolutePath());
                    } else {
                        log.warn("依赖文件不存在: " + artifact.getArtifactId());
                    }
                    break;
                }
            }
        }

        return result;
    }
}
