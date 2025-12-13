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
import org.objectweb.asm.*;
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
     * 插件ID
     */
    @Parameter(property = "plugin.id", defaultValue = "${project.artifactId}")
    private String pluginId;

    /**
     * 找到的扩展点实现类
     */
    private final List<String> extensionClasses = new ArrayList<>();
    
    /**
     * HttpHandler路由校验错误列表
     */
    private final List<String> routeValidationErrors = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("扫描扩展点实现类...");

        if (!classesDirectory.exists()) {
            getLog().warn("编译目录不存在: " + classesDirectory);
            return;
        }

        // 从 plugin.properties 中读取 pluginId
        resolvePluginId();

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
        
        // 检查路由校验错误
        if (!routeValidationErrors.isEmpty()) {
            getLog().error("========== HttpHandler 路由校验失败 ==========");
            for (String error : routeValidationErrors) {
                getLog().error(error);
            }
            getLog().error("==============================================");
            throw new MojoFailureException(
                "HttpHandler 路由校验失败！所有实现 HttpHandler 的 Controller 路由必须以 /plugin/" + pluginId + "/ 开头"
            );
        }
    }

    /**
     * 从 plugin.properties 文件读取 pluginId
     */
    private void resolvePluginId() throws MojoExecutionException {
        File propsFile = new File(classesDirectory, "plugin.properties");
        if (!propsFile.exists()) {
            getLog().warn("未找到 plugin.properties，使用默认 pluginId: " + pluginId);
            return;
        }

        try (FileInputStream fis = new FileInputStream(propsFile)) {
            java.util.Properties props = new java.util.Properties();
            props.load(fis);
            String id = props.getProperty("plugin.id");
            if (id != null && !id.trim().isEmpty()) {
                this.pluginId = id.trim();
                getLog().debug("从 plugin.properties 读取到 pluginId: " + this.pluginId);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("读取 plugin.properties 失败", e);
        }
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
                
                // 如果是 HttpHandler，校验路由前缀
                if ("com.cmsr.onebase.plugin.api.HttpHandler".equals(visitor.getImplementedExtensionPoint())) {
                    validateHttpHandlerRoutes(visitor, className);
                }
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
            throw new MojoExecutionException("生成扩展索引文件失败", e);
        }
    }

    /**
     * 校验 HttpHandler 的路由前缀
     */
    private void validateHttpHandlerRoutes(ExtensionClassVisitor visitor, String className) {
        List<String> mappingPaths = visitor.getRequestMappingPaths();
        String requiredPrefix = "/plugin/" + pluginId + "/";
        
        if (mappingPaths.isEmpty()) {
            String error = String.format(
                "  [错误] %s 实现了 HttpHandler，但未找到 @RequestMapping 注解！", 
                className
            );
            getLog().warn(error);
            routeValidationErrors.add(error);
            return;
        }
        
        for (String path : mappingPaths) {
            if (!path.startsWith(requiredPrefix)) {
                String error = String.format(
                    "  [错误] %s 的路由 '%s' 不符合规范！必须以 '%s' 开头",
                    className, path, requiredPrefix
                );
                getLog().error(error);
                routeValidationErrors.add(error);
            } else {
                getLog().info("  ✓ 路由校验通过: " + path);
            }
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
        private final List<String> requestMappingPaths = new ArrayList<>();

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
        
        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // 扫描类级别的 @RequestMapping 注解
            if ("Lorg/springframework/web/bind/annotation/RequestMapping;".equals(descriptor)) {
                return new AnnotationVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitArray(String name) {
                        if ("value".equals(name) || "path".equals(name)) {
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visit(String name, Object value) {
                                    if (value instanceof String) {
                                        requestMappingPaths.add((String) value);
                                    }
                                }
                            };
                        }
                        return null;
                    }
                    
                    @Override
                    public void visit(String name, Object value) {
                        if (("value".equals(name) || "path".equals(name)) && value instanceof String) {
                            requestMappingPaths.add((String) value);
                        }
                    }
                };
            }
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                                          String signature, String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    // 扫描方法级别的 @GetMapping、@PostMapping、@PutMapping、@DeleteMapping、@RequestMapping
                    if ("Lorg/springframework/web/bind/annotation/GetMapping;".equals(desc)
                        || "Lorg/springframework/web/bind/annotation/PostMapping;".equals(desc)
                        || "Lorg/springframework/web/bind/annotation/PutMapping;".equals(desc)
                        || "Lorg/springframework/web/bind/annotation/DeleteMapping;".equals(desc)
                        || "Lorg/springframework/web/bind/annotation/PatchMapping;".equals(desc)
                        || "Lorg/springframework/web/bind/annotation/RequestMapping;".equals(desc)) {
                        
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @Override
                            public AnnotationVisitor visitArray(String name) {
                                if ("value".equals(name) || "path".equals(name)) {
                                    return new AnnotationVisitor(Opcodes.ASM9) {
                                        @Override
                                        public void visit(String name, Object value) {
                                            if (value instanceof String) {
                                                requestMappingPaths.add((String) value);
                                            }
                                        }
                                    };
                                }
                                return null;
                            }
                            
                            @Override
                            public void visit(String name, Object value) {
                                if (("value".equals(name) || "path".equals(name)) && value instanceof String) {
                                    requestMappingPaths.add((String) value);
                                }
                            }
                        };
                    }
                    return null;
                }
            };
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
        
        public List<String> getRequestMappingPaths() {
            return requestMappingPaths;
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
