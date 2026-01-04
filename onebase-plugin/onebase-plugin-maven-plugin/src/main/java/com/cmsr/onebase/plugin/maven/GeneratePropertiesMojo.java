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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
 * @author chengyuansen
 * @date 2025-12-18
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
     * <p>
     * 使用Unicode转义格式处理中文字符，确保符合Java Properties规范（ISO-8859-1编码）
     * </p>
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
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(propertiesFile), StandardCharsets.ISO_8859_1)) {
            // 手动写入文件头
            writer.write("# OneBase Plugin Properties - Auto Generated\n");
            writer.write("# " + new java.util.Date().toString() + "\n");
            
            // 按照固定顺序写入属性，中文字符会自动转换为Unicode转义
            writeProperty(writer, "plugin.id", props.getProperty("plugin.id"));
            writeProperty(writer, "plugin.description", props.getProperty("plugin.description"));
            writeProperty(writer, "plugin.class", props.getProperty("plugin.class"));
            writeProperty(writer, "plugin.license", props.getProperty("plugin.license"));
            writeProperty(writer, "plugin.version", props.getProperty("plugin.version"));
            writeProperty(writer, "plugin.provider", props.getProperty("plugin.provider"));
            
            if (props.getProperty("plugin.dependencies") != null) {
                writeProperty(writer, "plugin.dependencies", props.getProperty("plugin.dependencies"));
            }
            if (props.getProperty("plugin.requires") != null) {
                writeProperty(writer, "plugin.requires", props.getProperty("plugin.requires"));
            }
            
            getLog().info("已生成: " + propertiesFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("无法写入plugin.properties", e);
        }
    }

    /**
     * 写入单个属性，将中文字符转换为Unicode转义格式
     *
     * @param writer 输出流
     * @param key    属性键
     * @param value  属性值
     * @throws IOException IO异常
     */
    private void writeProperty(OutputStreamWriter writer, String key, String value) throws IOException {
        if (value == null) {
            return;
        }
        writer.write(escapeUnicode(key));
        writer.write("=");
        writer.write(escapeUnicode(value));
        writer.write("\n");
    }

    /**
     * 将字符串中的非ASCII字符转换为Unicode转义格式
     * <p>
     * 转义格式为反斜杠u加4位十六进制数字
     * 符合Java Properties规范，确保中文等非Latin-1字符正确保存和读取
     * </p>
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeUnicode(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // ASCII可打印字符（32-126）直接输出，其他字符转为Unicode转义
            if (c >= 32 && c <= 126) {
                // 特殊字符需要转义
                if (c == '\\') {
                    sb.append("\\\\");
                } else if (c == '=') {
                    sb.append("\\=");
                } else if (c == ':') {
                    sb.append("\\:");
                } else if (c == '#') {
                    sb.append("\\#");
                } else if (c == '!') {
                    sb.append("\\!");
                } else {
                    sb.append(c);
                }
            } else {
                // 非ASCII字符转为Unicode转义
                sb.append(String.format("\\u%04x", (int) c));
            }
        }
        return sb.toString();
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
