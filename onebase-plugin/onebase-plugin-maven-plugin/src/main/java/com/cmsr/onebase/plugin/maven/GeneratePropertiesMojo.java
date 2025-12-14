package com.cmsr.onebase.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 插件属性生成器
 * <p>
 * 自动生成 plugin.properties 文件，包含插件ID、版本、描述等元数据。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Mojo(
        name = "generate-properties",
        defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class GeneratePropertiesMojo extends AbstractMojo {

    /**
     * Maven项目
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 输出目录
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * 插件ID（默认使用artifactId）
     */
    @Parameter(property = "plugin.id", defaultValue = "${project.artifactId}")
    private String pluginId;

    /**
     * 插件版本（默认使用项目版本）
     */
    @Parameter(property = "plugin.version", defaultValue = "${project.version}")
    private String pluginVersion;

    /**
     * 插件描述（默认使用项目描述）
     */
    @Parameter(property = "plugin.description", defaultValue = "${project.description}")
    private String pluginDescription;

    /**
     * 插件提供者（默认使用groupId）
     */
    @Parameter(property = "plugin.provider", defaultValue = "${project.groupId}")
    private String pluginProvider;

    /**
     * 插件许可证
     */
    @Parameter(property = "plugin.license", defaultValue = "Apache-2.0")
    private String pluginLicense;

    /**
     * 插件依赖（其他插件ID，逗号分隔）
     */
    @Parameter(property = "plugin.dependencies")
    private String pluginDependencies;

    /**
     * 所需的最低平台版本
     */
    @Parameter(property = "plugin.requires")
    private String pluginRequires;

    /**
     * 生成的Plugin类的包名
     */
    @Parameter(property = "generatedPackage", defaultValue = "${project.groupId}")
    private String generatedPackage;

    /**
     * 插件主类的完全限定名（如果手动创建了插件类，必须指定此参数）
     * <p>
     * 例如：com.cmsr.onebase.plugin.demo.DemoPlugin
     * </p>
     */
    @Parameter(property = "pluginMainClass")
    private String pluginMainClass;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("生成插件属性文件: plugin.properties");

        // 确保输出目录存在
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // 尝试自动检测已生成的Plugin类
        String detectedPackage = detectGeneratedPluginPackage();
        if (detectedPackage != null) {
            getLog().info("自动检测到已生成的插件类包名: " + detectedPackage);
            generatedPackage = detectedPackage;
        }

        // 生成plugin.properties
        generatePluginProperties();

        // 生成生成的Plugin类名到属性中
        String generatedPluginClass = generatedPackage + ".GeneratedPlugin";
        getLog().info("插件类: " + generatedPluginClass);
    }

    /**
     * 自动检测已生成的GeneratedPlugin类的包名
     */
    private String detectGeneratedPluginPackage() {
        File generatedSourcesDir = new File(project.getBuild().getDirectory(), "generated-sources/onebase-plugin");
        if (!generatedSourcesDir.exists()) {
            return null;
        }

        try {
            List<Path> javaFiles = new ArrayList<>();
            Files.walk(generatedSourcesDir.toPath())
                    .filter(path -> path.toString().endsWith("GeneratedPlugin.java"))
                    .forEach(javaFiles::add);

            if (!javaFiles.isEmpty()) {
                Path pluginFile = javaFiles.get(0);
                List<String> lines = Files.readAllLines(pluginFile);
                for (String line : lines) {
                    if (line.trim().startsWith("package ")) {
                        String packageName = line.trim()
                                .substring("package ".length())
                                .replace(";", "")
                                .trim();
                        return packageName;
                    }
                }
            }
        } catch (IOException e) {
            getLog().debug("无法自动检测插件类包名: " + e.getMessage());
        }

        return null;
    }

    /**
     * 生成plugin.properties文件
     */
    private void generatePluginProperties() throws MojoExecutionException {
        Properties props = new Properties();
        props.setProperty("plugin.id", pluginId);
        props.setProperty("plugin.version", pluginVersion);
        
        // 关键修复：优先使用配置的 pluginMainClass，如果没有配置则使用自动生成的类名
        String finalPluginClass;
        if (pluginMainClass != null && !pluginMainClass.trim().isEmpty()) {
            finalPluginClass = pluginMainClass.trim();
            getLog().info("使用配置的插件主类: " + finalPluginClass);
        } else {
            finalPluginClass = generatedPackage + ".GeneratedPlugin";
            getLog().info("使用自动生成的插件主类: " + finalPluginClass);
        }
        props.setProperty("plugin.class", finalPluginClass);

        if (pluginDescription != null && !pluginDescription.isEmpty()) {
            props.setProperty("plugin.description", pluginDescription);
        }
        if (pluginProvider != null && !pluginProvider.isEmpty()) {
            props.setProperty("plugin.provider", pluginProvider);
        }
        if (pluginLicense != null && !pluginLicense.isEmpty()) {
            props.setProperty("plugin.license", pluginLicense);
        }
        if (pluginDependencies != null && !pluginDependencies.isEmpty()) {
            props.setProperty("plugin.dependencies", pluginDependencies);
        }
        if (pluginRequires != null && !pluginRequires.isEmpty()) {
            props.setProperty("plugin.requires", pluginRequires);
        }

        File propertiesFile = new File(outputDirectory, "plugin.properties");
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            props.store(writer, "OneBase Plugin Properties - Auto Generated");
            getLog().info("已生成: " + propertiesFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("无法写入plugin.properties", e);
        }
    }

    // Getter/Setter for testing
    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
}
