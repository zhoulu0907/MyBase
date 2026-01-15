package com.cmsr.onebase.plugin.maven.mojo;

import com.cmsr.onebase.plugin.maven.constant.PluginPackagingConstants;
import com.cmsr.onebase.plugin.maven.util.DependencyFilterUtil;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 生成开发模式依赖类路径文件
 * <p>
 * 在编译阶段生成 dev-dependencies-classpath.txt 文件，包含插件所有运行时依赖的绝对路径。
 * 该文件用于 dev 模式下的热重载，确保 HotReloadManager 能够加载插件的第三方依赖。
 * </p>
 * <p>
 * 依赖筛选规则：
 * <ul>
 * <li>只包含 scope 为 compile 或 runtime 的依赖</li>
 * <li>排除 scope 为 provided、test、system 的依赖（及其传递依赖）</li>
 * <li>排除宿主核心模块（定义在 PluginPackagingConstants.HOST_PROVIDED_ARTIFACTS）</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-14
 */
@Mojo(name = "generate-dev-classpath", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class GenerateDevClasspathMojo extends AbstractMojo {

    /**
     * Maven项目
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 编译输出目录
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * 是否跳过生成
     */
    @Parameter(property = "plugin.dev-classpath.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("跳过生成开发依赖类路径文件（plugin.dev-classpath.skip=true）");
            return;
        }

        getLog().info("开始生成开发模式依赖类路径文件: " + project.getArtifactId());

        // 收集需要包含的依赖
        List<String> dependencyPaths = collectDependencyPaths();

        // 生成文件（即使没有依赖也生成空文件）
        File classpathFile = new File(outputDirectory, PluginPackagingConstants.DEV_DEPENDENCIES_CLASSPATH_FILE);
        try {
            writeDependencyPaths(classpathFile, dependencyPaths);
            getLog().info("成功生成依赖类路径文件: " + classpathFile.getAbsolutePath());
            if (dependencyPaths.isEmpty()) {
                getLog().info("没有运行时依赖，已生成空文件");
            } else {
                getLog().info("包含 " + dependencyPaths.size() + " 个运行时依赖");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("生成依赖类路径文件失败", e);
        }
    }

    /**
     * 收集需要包含的依赖路径
     * <p>
     * 使用 DependencyFilterUtil 统一处理依赖筛选逻辑
     * </p>
     */
    private List<String> collectDependencyPaths() {
        List<String> paths = new ArrayList<>();

        // 使用工具类收集依赖
        List<Artifact> dependencies = DependencyFilterUtil.collectRuntimeDependencies(project, getLog());

        // 转换为路径列表
        for (Artifact artifact : dependencies) {
            paths.add(artifact.getFile().getAbsolutePath());
        }

        return paths;
    }

    /**
     * 判断是否应该包含该依赖
     * <p>
     * 筛选规则：
     * 1. 只包含 compile 和 runtime scope
     * 2. 排除 provided、test、system scope（自动排除 provided 的传递依赖）
     * 3. 排除宿主核心模块
     * </p>
     */
    private boolean shouldIncludeDependency(Artifact artifact) {
        String scope = artifact.getScope();

        // 排除 provided 和 test scope
        // Maven 依赖传递规则：provided 的传递依赖也会被标记为 provided
        if ("provided".equals(scope) || "test".equals(scope) || "system".equals(scope)) {
            getLog().debug("排除 " + scope + " scope 依赖: " + artifact.getArtifactId());
            return false;
        }

        // 只包含 compile 和 runtime
        if (!"compile".equals(scope) && !"runtime".equals(scope)) {
            getLog().debug("排除非运行时依赖: " + artifact.getArtifactId() + " (scope: " + scope + ")");
            return false;
        }

        // 排除宿主核心模块
        if (PluginPackagingConstants.HOST_PROVIDED_ARTIFACTS.contains(artifact.getArtifactId())) {
            getLog().debug("排除宿主核心模块: " + artifact.getArtifactId());
            return false;
        }

        return true;
    }

    /**
     * 将依赖路径写入文件
     * <p>
     * 文件格式：每行一个绝对路径
     * </p>
     */
    private void writeDependencyPaths(File file, List<String> paths) throws IOException {
        // 确保输出目录存在
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String path : paths) {
                writer.write(path);
                writer.newLine();
            }
        }

        getLog().debug("已写入 " + paths.size() + " 个依赖路径到: " + file.getAbsolutePath());
    }

    // Getter/Setter for testing
    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
