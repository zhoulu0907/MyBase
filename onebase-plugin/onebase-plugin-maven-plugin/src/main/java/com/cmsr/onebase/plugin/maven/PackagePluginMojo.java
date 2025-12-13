package com.cmsr.onebase.plugin.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 插件打包器
 * <p>
 * 将插件项目打包成 ZIP 格式的插件包，包含：
 * <ul>
 *     <li>插件主 JAR 文件</li>
 *     <li>plugin.properties 属性文件</li>
 *     <li>lib/ 目录下的所有运行时依赖</li>
 * </ul>
 * </p>
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Mojo(
        name = "package-plugin",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        threadSafe = true
)
public class PackagePluginMojo extends AbstractMojo {

    private static final String PLUGIN_SDK_ARTIFACT_ID = "onebase-plugin-sdk";
    private static final int BUFFER_SIZE = 8192;

    /**
     * Maven项目
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 构建输出目录
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File buildDirectory;

    /**
     * 编译输出目录
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * 最终JAR文件名
     */
    @Parameter(defaultValue = "${project.build.finalName}.jar", required = true)
    private String finalName;

    /**
     * 是否跳过插件打包
     */
    @Parameter(property = "plugin.package.skip", defaultValue = "false")
    private boolean skip;

    /**
     * ZIP包输出目录
     */
    @Parameter(property = "plugin.package.outputDirectory", defaultValue = "${project.build.directory}")
    private File pluginOutputDirectory;

    /**
     * Maven项目助手（用于附加生成的artifacts）
     */
    @Component
    private MavenProjectHelper projectHelper;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("跳过插件打包（plugin.package.skip=true）");
            return;
        }

        getLog().info("开始打包插件: " + project.getArtifactId());

        // 验证主JAR是否存在
        File mainJar = new File(buildDirectory, finalName);
        if (!mainJar.exists()) {
            throw new MojoExecutionException("主JAR文件不存在: " + mainJar.getAbsolutePath() +
                    "。请确保 package 阶段已生成 JAR 文件。");
        }

        // 验证plugin.properties是否存在
        File pluginProperties = new File(outputDirectory, "plugin.properties");
        if (!pluginProperties.exists()) {
            throw new MojoExecutionException("plugin.properties 不存在: " + pluginProperties.getAbsolutePath() +
                    "。请确保 generate-properties goal 已执行。");
        }

        // 创建输出目录
        if (!pluginOutputDirectory.exists()) {
            pluginOutputDirectory.mkdirs();
        }

        // 生成ZIP文件
        String zipFileName = project.getArtifactId() + "-" + project.getVersion() + ".zip";
        File zipFile = new File(pluginOutputDirectory, zipFileName);

        try {
            createPluginZip(zipFile, mainJar, pluginProperties);
            getLog().info("插件打包完成: " + zipFile.getAbsolutePath() +
                    " (大小: " + formatFileSize(zipFile.length()) + ")");

            // 附加ZIP作为项目artifact
            projectHelper.attachArtifact(project, "zip", "plugin", zipFile);
        } catch (IOException e) {
            throw new MojoExecutionException("打包插件失败", e);
        }
    }

    /**
     * 创建插件ZIP包
     */
    private void createPluginZip(File zipFile, File mainJar, File pluginProperties) throws IOException {
        // PF4J 解压后会进入插件目录，所以ZIP内部不需要再套一层目录
        String baseDir = "";

        try (ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(new FileOutputStream(zipFile)))) {

            getLog().info("正在添加 plugin.properties");
            addFileToZip(zos, baseDir + "plugin.properties", pluginProperties);

            // 将主JAR添加到lib目录，确保PF4J的PluginClassLoader可以加载
            getLog().info("正在添加主JAR到lib目录: " + mainJar.getName());
            addFileToZip(zos, baseDir + "lib/" + mainJar.getName(), mainJar);

            // 添加运行时依赖
            Set<Artifact> dependencies = project.getArtifacts();
            int dependencyCount = 0;

            for (Artifact artifact : dependencies) {
                // 跳过非运行时依赖
                if (!isRuntimeScope(artifact.getScope())) {
                    continue;
                }

                // 排除 onebase-plugin-sdk
                if (PLUGIN_SDK_ARTIFACT_ID.equals(artifact.getArtifactId())) {
                    getLog().debug("排除 SDK 依赖: " + artifact.getArtifactId());
                    continue;
                }

                File artifactFile = artifact.getFile();
                if (artifactFile != null && artifactFile.exists()) {
                    String libPath = baseDir + "lib/" + artifactFile.getName();
                    addFileToZip(zos, libPath, artifactFile);
                    dependencyCount++;
                    getLog().debug("  添加依赖: " + artifactFile.getName());
                }
            }

            getLog().info("共添加 " + dependencyCount + " 个运行时依赖");
        }
    }

    /**
     * 添加文件到ZIP
     */
    private void addFileToZip(ZipOutputStream zos, String entryName, File file) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        entry.setTime(file.lastModified());
        entry.setSize(file.length());
        zos.putNextEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }

        zos.closeEntry();
    }

    /**
     * 判断是否为运行时依赖
     */
    private boolean isRuntimeScope(String scope) {
        return scope == null ||
                Artifact.SCOPE_COMPILE.equals(scope) ||
                Artifact.SCOPE_RUNTIME.equals(scope);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }

    // Getter/Setter for testing
    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setBuildDirectory(File buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public void setPluginOutputDirectory(File pluginOutputDirectory) {
        this.pluginOutputDirectory = pluginOutputDirectory;
    }
}
