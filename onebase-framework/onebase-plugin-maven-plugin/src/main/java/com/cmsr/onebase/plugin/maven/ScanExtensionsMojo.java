package com.cmsr.onebase.plugin.maven;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.api.HttpHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 扩展点扫描与服务文件生成器
 * <p>
 * 扫描编译后的类文件，找出实现了扩展点接口的类，
 * 生成 META-INF/extensions.idx 索引文件。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Mojo(
        name = "scan-extensions",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class ScanExtensionsMojo extends AbstractMojo {

    /**
     * 扩展点接口列表（内部类名格式）
     */
    private static final Set<String> EXTENSION_POINT_INTERFACES = new HashSet<>();

    static {
        EXTENSION_POINT_INTERFACES.add(toInternalName(CustomFunction.class));
        EXTENSION_POINT_INTERFACES.add(toInternalName(DataProcessor.class));
        EXTENSION_POINT_INTERFACES.add(toInternalName(EventListener.class));
        EXTENSION_POINT_INTERFACES.add(toInternalName(HttpHandler.class));
    }

    /**
     * Maven项目
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 编译输出目录
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;

    /**
     * 找到的扩展点实现类
     */
    private final List<String> extensionClasses = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("扫描扩展点实现类...");

        if (!classesDirectory.exists()) {
            getLog().warn("编译目录不存在: " + classesDirectory);
            return;
        }

        // 扫描所有class文件
        try (Stream<Path> paths = Files.walk(classesDirectory.toPath())) {
            paths.filter(path -> path.toString().endsWith(".class"))
                    .filter(path -> !path.toString().contains("GeneratedPlugin"))
                    .forEach(this::scanClass);
        } catch (IOException e) {
            throw new MojoExecutionException("扫描类文件失败", e);
        }

        // 生成extensions.idx
        if (!extensionClasses.isEmpty()) {
            generateExtensionsIndex();
        }

        getLog().info("共发现 " + extensionClasses.size() + " 个扩展点实现类");
    }

    /**
     * 扫描单个class文件
     */
    private void scanClass(Path classPath) {
        try (FileInputStream fis = new FileInputStream(classPath.toFile())) {
            ClassReader reader = new ClassReader(fis);
            ExtensionClassVisitor visitor = new ExtensionClassVisitor();
            reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            if (visitor.isExtension()) {
                String className = visitor.getClassName().replace('/', '.');
                extensionClasses.add(className);
                getLog().info("  发现扩展点: " + className + " 实现 " + visitor.getImplementedExtensionPoint());
            }
        } catch (IOException e) {
            getLog().warn("无法读取类文件: " + classPath, e);
        }
    }

    /**
     * 生成 META-INF/extensions.idx 文件
     */
    private void generateExtensionsIndex() throws MojoExecutionException {
        File metaInfDir = new File(classesDirectory, "META-INF");
        if (!metaInfDir.exists()) {
            metaInfDir.mkdirs();
        }

        File indexFile = new File(metaInfDir, "extensions.idx");
        try (PrintWriter writer = new PrintWriter(new FileWriter(indexFile))) {
            for (String className : extensionClasses) {
                writer.println(className);
            }
            getLog().info("已生成: " + indexFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("无法生成extensions.idx", e);
        }
    }

    /**
     * 将类转换为内部名称格式
     */
    private static String toInternalName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    /**
     * ASM类访问器 - 检测是否实现了扩展点接口
     */
    private class ExtensionClassVisitor extends ClassVisitor {
        private String className;
        private String implementedExtensionPoint;
        private boolean isAbstract;
        private boolean isInterface;

        public ExtensionClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            this.className = name;
            this.isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
            this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;

            if (interfaces != null) {
                for (String iface : interfaces) {
                    if (EXTENSION_POINT_INTERFACES.contains(iface)) {
                        this.implementedExtensionPoint = iface.replace('/', '.');
                        break;
                    }
                }
            }
        }

        public boolean isExtension() {
            return implementedExtensionPoint != null && !isAbstract && !isInterface;
        }

        public String getClassName() {
            return className;
        }

        public String getImplementedExtensionPoint() {
            return implementedExtensionPoint;
        }
    }

    // Getter/Setter for testing
    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory;
    }
}
