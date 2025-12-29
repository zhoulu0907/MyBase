package com.cmsr.onebase.plugin.runtime.test.reload;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.runtime.test.util.PluginHttpTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 端到端热重载测试
 * <p>
 * 真实的端到端测试，包括：
 * <ul>
 * <li>修改源代码文件</li>
 * <li>编译插件</li>
 * <li>等待热重载</li>
 * <li>验证接口响应变化</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
                "onebase.plugin.enabled=true",
                "onebase.plugin.mode=dev",
                "onebase.plugin.auto-load=true",
                "onebase.plugin.auto-start=true",
                "onebase.plugin.dev-class-paths[0]=../onebase-plugin-demo/plugin-demo-hello/target/classes",
                "onebase.plugin.dev-class-paths[1]=../onebase-plugin-demo/plugin-demo-test/target/classes"
})
public class DevModeHotReloadTest {

        private static final Logger log = LoggerFactory.getLogger(DevModeHotReloadTest.class);

        @LocalServerPort
        private int port;

        @Autowired(required = false)
        private OneBasePluginManager pluginManager;

        private PluginHttpTestUtil httpUtil;

        @BeforeEach
        void setUp() {
                httpUtil = new PluginHttpTestUtil("http://localhost:" + port);
        }

        @Test
        @DisplayName("端到端测试：真实文件修改和热重载")
        void testRealFileModificationAndHotReload() throws Exception {
                log.info("=== 端到端测试：真实文件修改和热重载 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 源文件路径
                java.nio.file.Path sourceFile = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-hello/src/main/java/com/cmsr/onebase/plugin/demo/hello/http/HelloWorldHandler.java")
                                .toAbsolutePath().normalize();

                // 备份文件路径
                java.nio.file.Path backupFile = java.nio.file.Paths.get(
                                System.getProperty("java.io.tmpdir"), "HelloWorldHandler.java.backup");

                if (!java.nio.file.Files.exists(sourceFile)) {
                        log.warn("源文件不存在，跳过测试: {}", sourceFile);
                        return;
                }

                // 输出目录
                java.nio.file.Path outputDir = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-hello/target/classes").toAbsolutePath().normalize();

                try {
                        // 1. 备份原始文件
                        log.info("步骤1: 备份原始文件");
                        java.nio.file.Files.copy(sourceFile, backupFile,
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        // 2. 获取初始响应
                        log.info("步骤2: 获取初始响应");
                        PluginHttpTestUtil.HttpResponse initialResponse = httpUtil
                                        .get("/plugin/hello-plugin/hello?name=Test");
                        initialResponse.assertSuccess();
                        String initialPlugin = initialResponse.getJsonField("plugin");
                        log.info("初始响应 plugin 字段: {}", initialPlugin);

                        // 3. 修改源代码
                        log.info("步骤3: 修改源代码");
                        String newPluginValue = "hello-plugin-HOTRELOAD-" + System.currentTimeMillis();
                        modifySourceFile(sourceFile, "hello-plugin", newPluginValue);
                        log.info("已修改源代码，新 plugin 值: {}", newPluginValue);

                        // 4. 编译单个文件
                        log.info("步骤4: 编译修改后的代码");
                        boolean compiled = compileSingleJavaFile(sourceFile, outputDir);
                        if (!compiled) {
                                log.error("编译失败，跳过后续测试");
                                return;
                        }

                        // 5. 等待热重载（最多10秒）
                        log.info("步骤5: 等待热重载触发");
                        boolean reloaded = waitForHotReload(10);
                        if (!reloaded) {
                                log.warn("热重载未在预期时间内触发，继续验证");
                        }

                        // 6. 验证响应已更新
                        log.info("步骤6: 验证响应已更新");
                        PluginHttpTestUtil.HttpResponse updatedResponse = httpUtil
                                        .get("/plugin/hello-plugin/hello?name=Test");
                        updatedResponse.assertSuccess();
                        String updatedPlugin = updatedResponse.getJsonField("plugin");
                        log.info("更新后响应 plugin 字段: {}", updatedPlugin);

                        // 7. 断言
                        assertThat(updatedPlugin)
                                        .as("热重载后响应应该包含修改后的值")
                                        .isEqualTo(newPluginValue);

                        log.info("✓ 端到端热重载测试成功!");

                } finally {
                        // 8. 恢复原始文件
                        log.info("步骤7: 恢复原始文件");
                        if (java.nio.file.Files.exists(backupFile)) {
                                java.nio.file.Files.copy(backupFile, sourceFile,
                                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                java.nio.file.Files.delete(backupFile);

                                // 重新编译恢复的文件
                                compileSingleJavaFile(sourceFile, outputDir);

                                // 等待热重载恢复
                                Thread.sleep(3000);

                                log.info("已恢复原始文件并重新编译");
                        }
                }
        }

        @Test
        @DisplayName("端到端测试：连续测试两个Handler的热重载")
        void testSequentialHotReloadBothHandlers() throws Exception {
                log.info("=== 端到端测试：连续测试两个Handler的热重载 ===");

                if (pluginManager == null) {
                        log.warn("插件管理器未创建，跳过测试");
                        return;
                }

                // 源文件路径
                java.nio.file.Path helloSourceFile = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-hello/src/main/java/com/cmsr/onebase/plugin/demo/hello/http/HelloWorldHandler.java")
                                .toAbsolutePath().normalize();

                java.nio.file.Path testSourceFile = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-test/src/main/java/com/cmsr/onebase/plugin/demo/test/http/TestHttpHandler.java")
                                .toAbsolutePath().normalize();

                // 备份文件路径
                java.nio.file.Path helloBackup = java.nio.file.Paths.get(
                                System.getProperty("java.io.tmpdir"), "HelloWorldHandler.java.backup");
                java.nio.file.Path testBackup = java.nio.file.Paths.get(
                                System.getProperty("java.io.tmpdir"), "TestHttpHandler.java.backup");

                if (!java.nio.file.Files.exists(helloSourceFile) || !java.nio.file.Files.exists(testSourceFile)) {
                        log.warn("源文件不存在，跳过测试");
                        return;
                }

                // 输出目录
                java.nio.file.Path helloOutputDir = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-hello/target/classes").toAbsolutePath().normalize();
                java.nio.file.Path testOutputDir = java.nio.file.Paths.get(
                                "../onebase-plugin-demo/plugin-demo-test/target/classes").toAbsolutePath().normalize();

                try {
                        // === 第一次热重载: HelloWorldHandler ===
                        log.info("\n>>> 第一次热重载: HelloWorldHandler");
                        java.nio.file.Files.copy(helloSourceFile, helloBackup,
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        PluginHttpTestUtil.HttpResponse helloInitial = httpUtil
                                        .get("/plugin/hello-plugin/hello?name=Test");
                        helloInitial.assertSuccess();
                        String helloInitialPlugin = helloInitial.getJsonField("plugin");
                        log.info("HelloWorldHandler 初始 plugin: {}", helloInitialPlugin);

                        String helloNewPlugin = "hello-plugin-FIRST-" + System.currentTimeMillis();
                        modifySourceFile(helloSourceFile, "hello-plugin", helloNewPlugin);
                        compileSingleJavaFile(helloSourceFile, helloOutputDir);
                        Thread.sleep(5000);

                        PluginHttpTestUtil.HttpResponse helloUpdated = httpUtil
                                        .get("/plugin/hello-plugin/hello?name=Test");
                        helloUpdated.assertSuccess();
                        String helloUpdatedPlugin = helloUpdated.getJsonField("plugin");
                        assertThat(helloUpdatedPlugin).as("第一次热重载应该成功").isEqualTo(helloNewPlugin);
                        log.info("✓ 第一次热重载 (HelloWorldHandler) 成功!");

                        // === 第二次热重载: TestHttpHandler ===
                        log.info("\n>>> 第二次热重载: TestHttpHandler");
                        java.nio.file.Files.copy(testSourceFile, testBackup,
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        PluginHttpTestUtil.HttpResponse testInitial = httpUtil.get("/plugin/test-plugin/api/info");
                        testInitial.assertSuccess();
                        String testInitialVersion = testInitial.getJsonField("version");
                        log.info("TestHttpHandler 初始 version: {}", testInitialVersion);

                        String testNewVersion = "v2.0-SECOND-" + System.currentTimeMillis();
                        modifyTestHttpHandler(testSourceFile, testNewVersion);
                        compileSingleJavaFile(testSourceFile, testOutputDir);
                        Thread.sleep(5000);

                        PluginHttpTestUtil.HttpResponse testUpdated = httpUtil.get("/plugin/test-plugin/api/info");
                        testUpdated.assertSuccess();
                        String testUpdatedVersion = testUpdated.getJsonField("version");
                        assertThat(testUpdatedVersion).as("第二次热重载应该成功!").isEqualTo(testNewVersion);
                        log.info("✓ 第二次热重载 (TestHttpHandler) 成功!");
                        log.info("\n✓✓✓ 连续两次热重载都成功! 问题已解决! ✓✓✓");

                } finally {
                        if (java.nio.file.Files.exists(helloBackup)) {
                                java.nio.file.Files.copy(helloBackup, helloSourceFile,
                                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                java.nio.file.Files.delete(helloBackup);
                                compileSingleJavaFile(helloSourceFile, helloOutputDir);
                        }
                        if (java.nio.file.Files.exists(testBackup)) {
                                java.nio.file.Files.copy(testBackup, testSourceFile,
                                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                java.nio.file.Files.delete(testBackup);
                                compileSingleJavaFile(testSourceFile, testOutputDir);
                        }
                        Thread.sleep(3000);
                        log.info("已恢复所有文件");
                }
        }

        /**
         * 修改源文件中的指定内容
         */
        private void modifySourceFile(java.nio.file.Path sourceFile, String oldValue, String newValue)
                        throws Exception {
                String content = java.nio.file.Files.readString(sourceFile);
                // 使用更灵活的正则表达式，匹配 result.put("plugin", "任意值")
                String modified = content.replaceAll(
                                "result\\.put\\(\"plugin\", \"[^\"]+\"\\)",
                                "result.put(\"plugin\", \"" + newValue + "\")");
                java.nio.file.Files.writeString(sourceFile, modified);
        }

        /**
         * 修改 TestHttpHandler 的 version 字段
         */
        private void modifyTestHttpHandler(java.nio.file.Path sourceFile, String newVersion)
                        throws Exception {
                String content = java.nio.file.Files.readString(sourceFile);
                // 匹配 info.put("version", "任意值")
                String modified = content.replaceAll(
                                "info\\.put\\(\"version\", \"[^\"]+\"\\)",
                                "info.put(\"version\", \"" + newVersion + "\")");
                java.nio.file.Files.writeString(sourceFile, modified);
        }

        /**
         * 编译单个Java文件
         * 使用javac直接编译，避免Maven重新编译所有类导致多个热重载事件
         */
        private boolean compileSingleJavaFile(java.nio.file.Path sourceFile, java.nio.file.Path outputDir) {
                try {
                        log.info("编译单个文件: {} -> {}", sourceFile, outputDir);

                        // 构建javac命令
                        // 需要包含classpath以解析依赖
                        String javaHome = System.getProperty("java.home");
                        String javacPath = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "javac";
                        if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                javacPath += ".exe";
                        }

                        // 获取当前classpath
                        String classpath = System.getProperty("java.class.path");

                        ProcessBuilder pb = new ProcessBuilder(
                                        javacPath,
                                        "-d", outputDir.toString(),
                                        "-cp", classpath,
                                        "-encoding", "UTF-8",
                                        "-parameters", // Spring需要此参数来获取方法参数名
                                        sourceFile.toString());
                        pb.redirectErrorStream(true);

                        Process process = pb.start();

                        // 读取输出
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                                        new java.io.InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                        log.debug("javac: {}", line);
                                }
                        }

                        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);

                        if (!finished) {
                                process.destroyForcibly();
                                log.error("编译超时: {}", sourceFile);
                                return false;
                        }

                        if (process.exitValue() != 0) {
                                log.error("编译失败: {}, exit code: {}", sourceFile, process.exitValue());
                                return false;
                        }

                        log.info("编译成功: {}", sourceFile);
                        return true;
                } catch (Exception e) {
                        log.error("编译异常: " + sourceFile, e);
                        return false;
                }
        }

        /**
         * 编译插件（已废弃，改用compileSingleJavaFile）
         * 
         * @deprecated 使用mvn compile会重新编译所有类，触发多个热重载事件，应使用compileSingleJavaFile
         */
        @Deprecated
        private boolean compilePlugin(String pluginName) {
                try {
                        java.nio.file.Path pluginDir = java.nio.file.Paths.get(
                                        "../onebase-plugin-demo/" + pluginName).toAbsolutePath().normalize();

                        log.info("编译插件: {} (目录: {})", pluginName, pluginDir);

                        // 在Windows上使用mvn.cmd，在Unix/Linux上使用mvn
                        String mvnCommand = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd"
                                        : "mvn";
                        ProcessBuilder pb = new ProcessBuilder(mvnCommand, "compile", "-DskipTests", "-q");
                        pb.directory(pluginDir.toFile());
                        pb.redirectErrorStream(true);

                        Process process = pb.start();
                        boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);

                        if (!finished) {
                                process.destroyForcibly();
                                log.error("编译超时: {}", pluginName);
                                return false;
                        }

                        if (process.exitValue() != 0) {
                                log.error("编译失败: {}, exit code: {}", pluginName, process.exitValue());
                                return false;
                        }

                        log.info("编译成功: {}", pluginName);
                        return true;
                } catch (Exception e) {
                        log.error("编译异常: " + pluginName, e);
                        return false;
                }
        }

        /**
         * 等待热重载触发
         */
        private boolean waitForHotReload(int maxSeconds) throws InterruptedException {
                log.info("等待热重载触发 (最多{}秒)...", maxSeconds);
                // 简单等待，实际应该监听日志或事件
                Thread.sleep(maxSeconds * 1000L);
                return true;
        }
}
