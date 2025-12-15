package com.cmsr.onebase.plugin.maven;

import com.cmsr.onebase.plugin.core.ExtensionPointConstants;
import com.cmsr.onebase.plugin.core.ExtensionPointScannerASM;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 扩展点扫描与服务文件生成器
 * <p>
 * 扫描编译后的类文件，找出实现了扩展点接口的类，
 * 生成 META-INF/extensions.idx 索引文件。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
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
        for (String className : ExtensionPointConstants.EXTENSION_POINT_CLASS_NAMES) {
            EXTENSION_POINT_INTERFACES.add(toInternalName(className));
        }
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
    private List<String> extensionClasses = new ArrayList<>();
    
    /**
     * HttpHandler路由校验错误列表
     */
    private final List<String> routeValidationErrors = new ArrayList<>();
    
    /**
     * 扩展点扫描器
     */
    private ExtensionPointScannerASM scanner = new ExtensionPointScannerASM();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("扫描扩展点实现类...");

        if (!classesDirectory.exists()) {
            getLog().warn("编译目录不存在: " + classesDirectory);
            return;
        }

        // 从 plugin.properties 中读取 pluginId
        resolvePluginId();

        // 使用ExtensionPointScannerASM扫描所有扩展点
        extensionClasses = scanner.scanExtensions(classesDirectory);
        
        // 输出扫描结果
        for (String className : extensionClasses) {
            getLog().info("  发现扩展点: " + className);
        }
        
        // 校验HttpHandler路由（需要额外处理）
        validateHttpHandlerRoutes();

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
     * 扫描单个class文件（已废弃，改用ExtensionPointScannerASM）
     */
    @Deprecated
    private void scanClass(File classFile) {
        // 保留方法签名以兼容，实际逻辑已移至ExtensionPointScannerASM
    }
    
    /**
     * 校验 HttpHandler 的路由前缀
     */
    private void validateHttpHandlerRoutes() throws MojoExecutionException {
        // 遍历所有扫描到的类，检查是否为HttpHandler
        for (String className : extensionClasses) {
            try {
                // 重新读取class文件以获取路由信息
                String classFilePath = className.replace('.', '/') + ".class";
                File classFile = new File(classesDirectory, classFilePath);
                
                if (!classFile.exists()) {
                    continue;
                }
                
                try (FileInputStream fis = new FileInputStream(classFile)) {
                    ClassReader reader = new ClassReader(fis);
                    HttpHandlerRouteValidator validator = new HttpHandlerRouteValidator(className);
                    reader.accept(validator, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
                    
                    if (validator.isHttpHandler()) {
                        validateRoutes(validator, className);
                    }
                }
            } catch (IOException e) {
                getLog().warn("无法读取类文件进行路由校验: " + className, e);
            }
        }
    }
    
    /**
     * 校验路由前缀
     */
    private void validateRoutes(HttpHandlerRouteValidator validator, String className) {
        List<String> mappingPaths = validator.getRequestMappingPaths();
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
     * 将类名转换为内部名称格式（用于兼容）
     */
    private static String toInternalName(String className) {
        return className.replace('.', '/');
    }

    /**
     * ASM类访问器 - 专门用于HttpHandler路由校验
     */
    private class HttpHandlerRouteValidator extends ClassVisitor {
        private String className;
        private boolean isHttpHandler = false;
        private final List<String> requestMappingPaths = new ArrayList<>();

        public HttpHandlerRouteValidator(String className) {
            super(Opcodes.ASM9);
            this.className = className;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            if (interfaces != null) {
                for (String iface : interfaces) {
                    if ("com/cmsr/onebase/plugin/api/HttpHandler".equals(iface)) {
                        this.isHttpHandler = true;
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
                    // 扫描方法级别的 @GetMapping、@PostMapping等
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

        public boolean isHttpHandler() {
            return isHttpHandler;
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
