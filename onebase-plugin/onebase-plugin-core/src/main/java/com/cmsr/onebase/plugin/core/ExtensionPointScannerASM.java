package com.cmsr.onebase.plugin.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * 扩展点扫描器（ASM实现）
 * <p>
 * 使用ASM字节码分析技术在编译时扫描.class文件，
 * 查找所有扩展点接口的实现类，用于Maven插件构建时生成索引。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-15
 */
public class ExtensionPointScannerASM {

    private static final Logger log = LoggerFactory.getLogger(ExtensionPointScannerASM.class);

    /**
     * 扩展点接口的内部类名集合（ASM格式：com/cmsr/onebase/plugin/sdk/data/DataProcessor）
     */
    private static final Set<String> EXTENSION_POINT_INTERNAL_NAMES = new HashSet<>();

    static {
        for (String className : ExtensionPointConstants.EXTENSION_POINT_CLASS_NAMES) {
            EXTENSION_POINT_INTERNAL_NAMES.add(toInternalName(className));
        }
    }

    /**
     * 扫描指定目录下的所有class文件，查找扩展点实现类
     *
     * @param classesDirectory 编译输出目录
     * @return 扩展点实现类的完整类名列表
     */
    public List<String> scanExtensions(File classesDirectory) {
        List<String> extensionClasses = new ArrayList<>();

        if (!classesDirectory.exists() || !classesDirectory.isDirectory()) {
            log.warn("编译目录不存在或不是目录: {}", classesDirectory);
            return extensionClasses;
        }

        try (Stream<Path> paths = Files.walk(classesDirectory.toPath())) {
            paths.filter(path -> path.toString().endsWith(".class"))
                    .filter(path -> !path.toString().contains("GeneratedPlugin"))
                    .forEach(path -> {
                        String className = scanClassFile(path.toFile());
                        if (className != null) {
                            extensionClasses.add(className);
                            log.debug("发现扩展点实现类: {}", className);
                        }
                    });
        } catch (IOException e) {
            log.error("扫描class文件失败", e);
        }

        log.info("共发现 {} 个扩展点实现类", extensionClasses.size());
        return extensionClasses;
    }

    /**
     * 扫描单个class文件，判断是否实现了扩展点接口
     *
     * @param classFile class文件
     * @return 如果实现了扩展点接口，返回完整类名；否则返回null
     */
    private String scanClassFile(File classFile) {
        try (FileInputStream fis = new FileInputStream(classFile)) {
            ClassReader cr = new ClassReader(fis);
            ExtensionClassVisitor visitor = new ExtensionClassVisitor();
            cr.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            if (visitor.isExtension()) {
                return visitor.getClassName();
            }
        } catch (IOException e) {
            log.warn("读取class文件失败: {}", classFile, e);
        }

        return null;
    }

    /**
     * 将Java类名转换为ASM内部类名格式
     * 例如: com.cmsr.onebase.Plugin -> com/cmsr/onebase/Plugin
     */
    private static String toInternalName(String className) {
        return className.replace('.', '/');
    }

    /**
     * 将ASM内部类名转换为Java类名格式
     * 例如: com/cmsr/onebase/Plugin -> com.cmsr.onebase.Plugin
     */
    private static String toClassName(String internalName) {
        return internalName.replace('/', '.');
    }

    /**
     * ASM ClassVisitor，用于检测类是否实现了扩展点接口
     */
    private static class ExtensionClassVisitor extends ClassVisitor {

        private String className;
        private boolean isExtension = false;

        public ExtensionClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            this.className = toClassName(name);

            // 检查是否实现了扩展点接口
            if (interfaces != null) {
                for (String iface : interfaces) {
                    if (EXTENSION_POINT_INTERNAL_NAMES.contains(iface)) {
                        this.isExtension = true;
                        log.debug("类 {} 实现了扩展点接口: {}", className, toClassName(iface));
                        break;
                    }
                }
            }

            super.visit(version, access, name, signature, superName, interfaces);
        }

        public boolean isExtension() {
            return isExtension;
        }

        public String getClassName() {
            return className;
        }
    }
}
