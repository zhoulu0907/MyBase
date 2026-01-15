package com.cmsr.onebase.plugin.runtime.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 插件测试环境管理器
 * <p>
 * 统一管理插件测试环境的生命周期,包括:
 * <ul>
 * <li>创建测试目录</li>
 * <li>编译打包插件</li>
 * <li>复制插件到测试目录</li>
 * <li>测试后清理环境</li>
 * </ul>
 * </p>
 * <p>
 * 使用方式:
 * 
 * <pre>
 * &#64;BeforeAll
 * static void setupEnvironment() throws Exception {
 *     PluginTestEnvironmentManager.setupEnvironment();
 * }
 *
 * &#64;AfterAll
 * static void cleanupEnvironment() throws Exception {
 *     PluginTestEnvironmentManager.cleanupEnvironment();
 * }
 * </pre>
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-07
 */
public class PluginTestEnvironmentManager {

    private static final Logger log = LoggerFactory.getLogger(PluginTestEnvironmentManager.class);

    /**
     * 插件目录名称(相对于项目根目录)
     */
    private static final String PLUGINS_DIR = "plugins";

    /**
     * 需要打包的插件模块路径(相对于项目根目录)
     */
    private static final String[] PLUGIN_MODULES = {
            "onebase-plugin/onebase-plugin-demo/plugin-demo-hello",
            "onebase-plugin/onebase-plugin-demo/plugin-demo-test"
    };

    /**
     * 项目根目录
     */
    private static Path projectRoot;

    /**
     * 插件目录路径
     */
    private static Path pluginsDir;

    /**
     * 目录是否由测试创建
     */
    private static boolean dirCreatedByTest = false;

    /**
     * 测试前目录中已存在的文件
     */
    private static Set<String> existingFiles = new HashSet<>();

    /**
     * 测试期间复制的文件
     */
    private static List<Path> copiedFiles = new ArrayList<>();

    /**
     * 准备测试环境
     * <p>
     * 执行以下操作:
     * <ol>
     * <li>定位项目根目录</li>
     * <li>检查并创建插件目录</li>
     * <li>编译打包插件</li>
     * <li>复制插件到测试目录</li>
     * </ol>
     * </p>
     *
     * @throws Exception 如果环境准备失败
     */
    public static void setupEnvironment() throws Exception {
        log.info("========================================");
        log.info("开始准备插件测试环境");
        log.info("========================================");

        // 1. 定位项目根目录
        locateProjectRoot();

        // 2. 检查并创建插件目录
        setupPluginsDirectory();

        // 3. 编译打包插件
        buildPlugin();

        // 4. 复制插件到测试目录
        copyPluginToTestDirectory();

        log.info("========================================");
        log.info("✓ 插件测试环境准备完成");
        log.info("========================================\n");
    }

    /**
     * 插件管理器引用
     */
    private static com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager pluginManager;

    /**
     * 是否删除物理文件(默认 true)
     * <p>
     * 对于 ProdModeApiTest/StagingModeApiTest: 设置为 true,删除物理文件<br>
     * 对于 PluginLifecycleTest/PluginExceptionScenariosTest: 设置为 false,只卸载不删除
     * </p>
     */
    private static boolean deletePhysicalFiles = true;

    /**
     * 设置是否删除物理文件
     *
     * @param delete true=删除物理文件, false=只卸载不删除
     */
    public static void setDeletePhysicalFiles(boolean delete) {
        deletePhysicalFiles = delete;
    }

    /**
     * 设置插件管理器
     * <p>
     * 在测试方法中注入插件管理器,以便在清理环境时停止并卸载插件,释放文件锁
     * </p>
     *
     * @param manager 插件管理器
     */
    public static void setPluginManager(com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager manager) {
        pluginManager = manager;
    }

    /**
     * 清理测试环境
     * <p>
     * 执行以下操作:
     * <ol>
     * <li>停止并卸载所有插件(如果存在),释放文件锁</li>
     * <li>删除测试期间复制的插件文件(ZIP)</li>
     * <li>删除对应的解压目录(如果存在)</li>
     * <li>如果目录是测试创建的,递归删除整个目录</li>
     * <li>如果目录原本存在,只删除测试添加的文件</li>
     * </ol>
     * </p>
     *
     * @throws Exception 如果清理失败
     */
    public static void cleanupEnvironment() throws Exception {
        log.info("========================================");
        log.info("开始清理插件测试环境");
        log.info("========================================");

        // 1. 尝试停止并卸载插件以释放文件锁(特别是 Windows 环境)
        if (pluginManager != null) {
            try {
                // 先收集所有插件ID,避免在迭代时修改集合
                java.util.List<String> pluginIds = pluginManager.getPlugins().stream()
                        .map(org.pf4j.PluginWrapper::getPluginId)
                        .collect(java.util.stream.Collectors.toList());

                log.info("准备停止并卸载 {} 个插件", pluginIds.size());

                // 停止所有插件
                for (String pluginId : pluginIds) {
                    try {
                        org.pf4j.PluginState state = pluginManager.getPluginState(pluginId);
                        if (state == org.pf4j.PluginState.STARTED) {
                            pluginManager.stopPlugin(pluginId);
                            log.info("✓ 已停止插件: {}", pluginId);
                        }
                    } catch (Exception e) {
                        log.warn("! 停止插件失败: {} - {}", pluginId, e.getMessage(), e);
                    }
                }

                // 删除或卸载所有插件
                for (String pluginId : pluginIds) {
                    try {
                        if (deletePhysicalFiles) {
                            // 删除插件(自动停止、卸载并删除物理文件)
                            boolean deleted = pluginManager.deletePlugin(pluginId);
                            if (deleted) {
                                log.info("✓ 已删除插件: {}", pluginId);
                            } else {
                                log.warn("! 删除插件失败(返回false): {}", pluginId);
                            }
                        } else {
                            // 只卸载插件(不删除物理文件)
                            boolean unloaded = pluginManager.unloadPlugin(pluginId);
                            if (unloaded) {
                                log.info("✓ 已卸载插件: {}", pluginId);
                            } else {
                                log.warn("! 卸载插件失败(返回false): {}", pluginId);
                            }
                        }
                    } catch (Exception e) {
                        String action = deletePhysicalFiles ? "删除" : "卸载";
                        log.warn("! {}插件异常: {} - {}", action, pluginId, e.getMessage(), e);
                    }
                }

                log.info("✓ 已完成所有插件的停止和卸载");
            } catch (Exception e) {
                log.warn("! 插件清理过程异常: {}", e.getMessage(), e);
            }
        }

        // 建议 GC 多次,帮助释放未引用的资源
        System.gc();
        Thread.sleep(200);
        System.gc();
        Thread.sleep(200);
        System.gc();
        // 稍作等待让操作系统释放句柄
        Thread.sleep(300);

        if (pluginsDir == null || !Files.exists(pluginsDir)) {
            log.info("插件目录不存在,无需清理");
            return;
        }

        // 2. 如果目录是测试创建的,递归删除整个目录
        if (dirCreatedByTest) {
            try {
                deleteRecursively(pluginsDir);
                log.info("✓ 递归删除测试目录: {}", PLUGINS_DIR);
            } catch (IOException e) {
                log.warn("! 删除目录失败: {} - {}", PLUGINS_DIR, e.getMessage());
            }
        } else {
            // 3. 如果目录原本存在,只删除测试相关文件
            log.info("目录原本存在,保留目录: {}", PLUGINS_DIR);

            for (Path file : copiedFiles) {
                if (Files.exists(file)) {
                    // 3.1 删除 ZIP 文件
                    forceDelete(file);

                    // 3.2 尝试删除对应的解压目录
                    // 假设解压目录名为文件名(去掉 .zip 或 .jar 扩展名)
                    String fileName = file.getFileName().toString();
                    if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
                        String dirName = fileName.substring(0, fileName.lastIndexOf('.'));
                        Path unzipDir = pluginsDir.resolve(dirName);
                        if (Files.exists(unzipDir) && Files.isDirectory(unzipDir)) {
                            try {
                                deleteRecursively(unzipDir);
                                log.info("✓ 删除解压目录: {}", dirName);
                            } catch (IOException e) {
                                log.warn("! 删除解压目录失败: {} - {}", dirName, e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        // 4. 重置状态
        copiedFiles.clear();
        existingFiles.clear();
        dirCreatedByTest = false;
        pluginManager = null;

        log.info("========================================");
        log.info("✓ 插件测试环境清理完成");
        log.info("========================================\n");
    }

    /**
     * 强制删除文件(带重试)
     */
    private static void forceDelete(Path path) throws IOException {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                Files.delete(path);
                log.info("✓ 删除文件: {}", path.getFileName());
                return;
            } catch (IOException e) {
                if (i == maxRetries - 1) {
                    log.warn("! 删除文件失败(已重试{}次): {} - {}", maxRetries, path.getFileName(), e.getMessage());
                    throw e;
                }
                try {
                    Thread.sleep(500); // 等待文件释放
                    System.gc(); // 再次尝试 GC
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 递归删除文件和目录(带重试)
     * 
     * @param path 要删除的路径
     * @throws IOException 如果删除失败
     */
    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                for (Path entry : entries.collect(Collectors.toList())) {
                    deleteRecursively(entry);
                }
            }
        }

        // 尝试删除自身
        try {
            Files.delete(path);
        } catch (IOException e) {
            // 简单重试逻辑
            try {
                Thread.sleep(200);
                Files.delete(path);
            } catch (Exception retryEx) {
                // 如果是目录且不空,可能上面的递归删除部分失败了
                throw e;
            }
        }
    }

    /**
     * 确保插件文件存在
     * <p>
     * 用于在测试方法间检查插件文件是否存在,如果不存在则重新复制
     * 适用于使用 @DirtiesContext(AFTER_EACH_TEST_METHOD) 的测试类
     * </p>
     *
     * @throws Exception 如果操作失败
     */
    public static void ensurePluginExists() throws Exception {
        if (pluginsDir == null) {
            log.warn("插件目录未初始化,跳过检查");
            return;
        }

        // 检查目录中是否有 ZIP 文件
        boolean hasZipFiles = false;
        if (Files.exists(pluginsDir)) {
            try (Stream<Path> files = Files.list(pluginsDir)) {
                hasZipFiles = files.anyMatch(p -> p.getFileName().toString().endsWith(".zip"));
            }
        }

        if (hasZipFiles) {
            // 插件文件已存在,无需操作
            return;
        }

        log.info("检测到插件文件不存在,重新复制插件包");

        // 重新复制插件
        copyPluginToTestDirectory();
    }

    /**
     * 定位项目根目录
     * <p>
     * 从当前工作目录向上查找,直到找到包含 pom.xml 的目录
     * </p>
     *
     * @throws IOException 如果无法定位项目根目录
     */
    private static void locateProjectRoot() throws IOException {
        Path current = Paths.get("").toAbsolutePath();
        log.info("当前工作目录: {}", current);

        // 向上查找项目根目录(包含 onebase-plugin 目录的父目录)
        while (current != null) {
            Path onebasePluginDir = current.resolve("onebase-plugin");
            if (Files.exists(onebasePluginDir) && Files.isDirectory(onebasePluginDir)) {
                projectRoot = current;
                log.info("✓ 定位项目根目录: {}", projectRoot);
                return;
            }
            current = current.getParent();
        }

        throw new IOException("无法定位项目根目录,请确保在项目目录下运行测试");
    }

    /**
     * 设置插件目录
     * <p>
     * 检查目录是否存在,不存在则创建,并记录初始状态
     * 这里的目录相对于当前工作目录(即 Maven 运行测试时的目录)
     * </p>
     *
     * @throws IOException 如果目录操作失败
     */
    private static void setupPluginsDirectory() throws IOException {
        // 使用当期工作目录下的 plugins 目录,与配置 onebase.plugin.plugins-dir=plugins 保持一致
        pluginsDir = Paths.get(PLUGINS_DIR).toAbsolutePath();

        if (Files.exists(pluginsDir)) {
            log.info("插件目录已存在: {}", pluginsDir);
            dirCreatedByTest = false;

            // 记录已存在的文件
            try (Stream<Path> files = Files.list(pluginsDir)) {
                existingFiles = files
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toSet());
                log.info("目录中已有 {} 个文件", existingFiles.size());
            }
        } else {
            log.info("创建插件目录: {}", pluginsDir);
            Files.createDirectories(pluginsDir);
            dirCreatedByTest = true;
            log.info("✓ 插件目录创建成功");
        }
    }

    /**
     * 编译打包插件
     * <p>
     * 使用 Maven 命令编译打包所有插件模块
     * </p>
     *
     * @throws Exception 如果编译失败
     */
    private static void buildPlugin() throws Exception {
        log.info("========================================");
        log.info("开始编译打包 {} 个插件模块", PLUGIN_MODULES.length);
        log.info("========================================");

        // 构建 Maven 命令 - 一次性编译所有插件模块
        List<String> command = new ArrayList<>();

        // Windows 环境使用 mvn.cmd, Linux/Mac 使用 mvn
        String mvnCommand = System.getProperty("os.name").toLowerCase().contains("windows")
                ? "mvn.cmd"
                : "mvn";

        command.add(mvnCommand);
        command.add("clean");
        command.add("package");
        command.add("-pl");
        // 用逗号连接所有模块路径
        command.add(String.join(",", PLUGIN_MODULES));
        command.add("-am");
        command.add("-DskipTests");

        log.info("执行命令: {}", String.join(" ", command));

        // 执行 Maven 命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(projectRoot.toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 读取输出
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "GBK"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 只输出关键信息,避免日志过多
                if (line.contains("BUILD SUCCESS") || line.contains("BUILD FAILURE")
                        || line.contains("ERROR") || line.contains("WARNING")) {
                    log.info("  {}", line);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("插件编译失败,退出码: " + exitCode);
        }

        log.info("✓ 所有插件编译成功");
    }

    /**
     * 复制插件到测试目录
     * <p>
     * 从所有插件模块的 target 目录查找所有 ZIP 包并复制到测试目录,保持原始文件名
     * </p>
     *
     * @throws IOException 如果复制失败
     */
    private static void copyPluginToTestDirectory() throws IOException {
        int totalCopied = 0;

        // 遍历所有插件模块
        for (String pluginModule : PLUGIN_MODULES) {
            Path pluginModuleDir = projectRoot.resolve(pluginModule);
            Path targetDir = pluginModuleDir.resolve("target");

            if (!Files.exists(targetDir)) {
                log.warn("插件 target 目录不存在,跳过: {}", targetDir);
                continue;
            }

            // 查找所有 ZIP 文件
            List<Path> zipFiles;
            try (Stream<Path> files = Files.list(targetDir)) {
                zipFiles = files
                        .filter(p -> p.getFileName().toString().endsWith(".zip"))
                        .collect(Collectors.toList());
            }

            if (zipFiles.isEmpty()) {
                log.warn("未找到插件 ZIP 包: {}", targetDir);
                continue;
            }

            log.info("在 {} 中找到 {} 个插件包", pluginModule, zipFiles.size());

            // 复制所有 ZIP 文件到测试目录,保持原始文件名
            for (Path sourceZip : zipFiles) {
                String fileName = sourceZip.getFileName().toString();
                Path targetZip = pluginsDir.resolve(fileName);

                Files.copy(sourceZip, targetZip, StandardCopyOption.REPLACE_EXISTING);
                copiedFiles.add(targetZip);
                totalCopied++;

                log.info("✓ 插件复制成功: {}", fileName);
            }
        }

        if (totalCopied == 0) {
            throw new IOException("未找到任何插件 ZIP 包");
        }

        log.info("✓ 共复制 {} 个插件包到测试目录", totalCopied);
    }

    /**
     * 获取插件目录路径
     *
     * @return 插件目录路径
     */
    public static Path getPluginsDir() {
        return pluginsDir;
    }

    /**
     * 获取项目根目录路径
     *
     * @return 项目根目录路径
     */
    public static Path getProjectRoot() {
        return projectRoot;
    }
}
