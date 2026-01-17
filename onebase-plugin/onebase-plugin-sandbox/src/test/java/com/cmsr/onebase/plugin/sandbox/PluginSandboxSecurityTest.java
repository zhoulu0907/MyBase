package com.cmsr.onebase.plugin.sandbox;

import com.cmsr.onebase.plugin.sandbox.config.SandboxAutoConfiguration;
import com.cmsr.onebase.plugin.sandbox.config.SandboxProperties;
import com.cmsr.onebase.plugin.sandbox.executor.PluginSandboxExecutor;
import com.cmsr.onebase.plugin.sandbox.manager.PluginSecurityManager;
import com.cmsr.onebase.plugin.sandbox.model.PluginPermissions;
import com.cmsr.onebase.plugin.sandbox.model.SandboxResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

/**
 * 插件沙箱安全测试
 * <p>
 * 验证沙箱的各种安全控制功能
 * </p>
 *
 * @author OneBase Plugin Team
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest(classes = {
        SandboxAutoConfiguration.class,
        SandboxProperties.class
})
public class PluginSandboxSecurityTest {

    private PluginSecurityManager securityManager;
    private PluginSandboxExecutor executor;
    private SecurityManager originalSecurityManager;

    @BeforeEach
    void setUp() {
        // 保存原始安全管理器
        originalSecurityManager = System.getSecurityManager();

        // 创建并安装插件安全管理器
        securityManager = new PluginSecurityManager();
        System.setSecurityManager(securityManager);

        executor = new PluginSandboxExecutor(securityManager, 10);
    }

    @AfterEach
    void tearDown() {
        // 恢复原始安全管理器
        System.setSecurityManager(originalSecurityManager);

        // 关闭执行器
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("场景1: 文件读取控制 - 应该阻止")
    void testFileReadControl_ShouldBlock() {
        // Given: 配置禁止读文件
        PluginPermissions permissions = PluginPermissions.strict();

        // When: 尝试读取文件
        SandboxResult<String> result = executor.execute("test-plugin", () -> {
            return Files.readString(
                    new File("/etc/passwd").toPath()
            ).substring(0, 100);  // 只读取前100个字符
        }, permissions);

        // Then: 应该被阻止
        assertFalse(result.isSuccess());
        assertTrue(result.isSecurityViolation());
        assertTrue(result.getError().contains("禁止读文件"));
        log.info("测试通过: 文件读取被正确阻止");
    }

    @Test
    @DisplayName("场景2: 文件写入控制 - 应该阻止")
    void testFileWriteControl_ShouldBlock() {
        // Given: 配置禁止写文件
        PluginPermissions permissions = PluginPermissions.strict();

        // When: 尝试写文件
        SandboxResult<Void> result = executor.execute("test-plugin", () -> {
            Files.writeString(
                    new File("/tmp/test-plugin-sandbox.txt").toPath(),
                    "test content"
            );
            return null;
        }, permissions);

        // Then: 应该被阻止
        assertFalse(result.isSuccess());
        assertTrue(result.isSecurityViolation());
        assertTrue(result.getError().contains("禁止写文件"));
        log.info("测试通过: 文件写入被正确阻止");
    }

    @Test
    @DisplayName("场景3: 网络访问控制 - 应该阻止")
    void testNetworkAccessControl_ShouldBlock() {
        // Given: 配置禁止网络
        PluginPermissions permissions = PluginPermissions.strict();

        // When: 尝试访问网络（使用 Socket 直接连接到本地端口，确保权限检查被触发）
        SandboxResult<String> result = executor.execute("test-plugin", () -> {
            // 尝试连接到本地回环地址（会触发 SocketPermission 检查）
            java.net.Socket socket = new java.net.Socket("127.0.0.1", 8080);
            socket.close();
            return "success";
        }, permissions);

        // Then: 应该被阻止
        assertFalse(result.isSuccess());
        assertTrue(result.isSecurityViolation());
        // 网络操作可能被 PropertyPermission 检查阻止（InetAddress 初始化时）
        // 或者被 SocketPermission 检查阻止
        assertTrue(result.getError().contains("禁止"));
        log.info("测试通过: 网络访问被正确阻止");
    }

    @Test
    @DisplayName("场景4: 反射访问控制 - 应该阻止")
    void testReflectionAccessControl_ShouldBlock() {
        // Given: 配置禁止反射
        PluginPermissions permissions = PluginPermissions.strict();

        // When: 尝试反射访问私有字段
        SandboxResult<String> result = executor.execute("test-plugin", () -> {
            Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            return "success";
        }, permissions);

        // Then: 应该被阻止
        assertFalse(result.isSuccess());
        assertTrue(result.isSecurityViolation());
        assertTrue(result.getError().contains("禁止反射操作"));
        log.info("测试通过: 反射访问被正确阻止");
    }

    @Test
    @DisplayName("场景5: 执行超时控制 - 应该中断")
    void testExecutionTimeoutControl_ShouldInterrupt() {
        // Given: 配置很短的超时时间
        PluginPermissions permissions = PluginPermissions.strict();
        permissions.setMaxExecutionTime(1000);  // 1秒

        // When: 执行死循环
        SandboxResult<Void> result = executor.execute("test-plugin", () -> {
            try {
                Thread.sleep(2000);  // 模拟耗时操作
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        }, permissions);

        // Then: 应该超时
        assertFalse(result.isSuccess());
        assertTrue(result.isTimeout());
        assertTrue(result.getError().contains("执行超时"));
        log.info("测试通过: 执行超时被正确控制");
    }

    @Test
    @DisplayName("场景6: 允许文件读取 - 应该成功")
    void testFileReadAllowed_ShouldSucceed() {
        // Given: 配置允许读文件
        PluginPermissions permissions = new PluginPermissions();
        permissions.setAllowFileRead(true);

        // When: 尝试读取允许的文件
        SandboxResult<String> result = executor.execute("test-plugin", () -> {
            // 读取项目根目录的 README 文件
            File readmeFile = new File("README.md");
            if (readmeFile.exists()) {
                return Files.readString(readmeFile.toPath()).substring(0, 100);
            }
            return "File not found";
        }, permissions);

        // Then: 应该成功
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        log.info("测试通过: 允许的文件读取操作成功");
    }

    @Test
    @DisplayName("场景7: 多线程数限制 - 应该阻止")
    void testThreadCountLimit_ShouldBlock() {
        // Given: 配置最大1个线程
        PluginPermissions permissions = PluginPermissions.strict();
        permissions.setMaxThreads(1);

        // When: 同时启动多个线程
        final var latch = new java.util.concurrent.CountDownLatch(3);
        final var results = new java.util.concurrent.CopyOnWriteArrayList<SandboxResult<Void>>();

        for (int i = 0; i < 3; i++) {
            final int index = i;
            new Thread(() -> {
                SandboxResult<Void> result = executor.execute("test-plugin", () -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                }, permissions);
                results.add(result);
                latch.countDown();
            }).start();
        }

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then: 应该有线程被阻止
        long blockedCount = results.stream()
                .filter(r -> !r.isSuccess() && r.getError().contains("超过最大线程数"))
                .count();
        assertTrue(blockedCount > 0, "应该有线程因超过最大线程数被阻止");
        log.info("测试通过: 线程数限制被正确控制，被阻止的线程数: {}", blockedCount);
    }

    @Test
    @DisplayName("场景8: 宿主代码不受限制")
    void testHostCodeNotRestricted() {
        // Given: 不在沙箱中

        // When: 宿主代码执行文件操作
        try {
            Files.writeString(
                    new File("/tmp/host-test.txt").toPath(),
                    "host code"
            );
            String content = Files.readString(
                    new File("/tmp/host-test.txt").toPath()
            );

            // Then: 应该成功（宿主代码不受限制）
            assertEquals("host code", content);
            log.info("测试通过: 宿主代码不受沙箱限制");

        } catch (Exception e) {
            fail("宿主代码不应该被沙箱限制: " + e.getMessage());
        } finally {
            // 清理
            new File("/tmp/host-test.txt").delete();
        }
    }

    @Test
    @DisplayName("场景9: 安全管理器状态检查")
    void testSecurityManagerStateCheck() {
        // Given: 默认状态
        assertFalse(securityManager.isInSandbox());
        assertNull(securityManager.getCurrentPluginId());

        // When: 进入沙箱
        securityManager.enterSandbox("test-plugin");

        try {
            // Then: 状态应该更新
            assertTrue(securityManager.isInSandbox());
            assertEquals("test-plugin", securityManager.getCurrentPluginId());
            log.info("测试通过: 安全管理器状态正确");

        } finally {
            // 清理
            securityManager.exitSandbox();
        }
    }

    @Test
    @DisplayName("场景10: 权限配置的继承和覆盖")
    void testPermissionConfiguration() {
        // Given: 配置插件权限
        PluginPermissions permissions1 = PluginPermissions.strict();
        permissions1.setAllowFileRead(true);
        securityManager.configurePlugin("plugin-1", permissions1);

        PluginPermissions permissions2 = PluginPermissions.permissive();
        securityManager.configurePlugin("plugin-2", permissions2);

        // When: 获取权限配置
        PluginPermissions retrieved1 = securityManager.getPermissions("plugin-1");
        PluginPermissions retrieved2 = securityManager.getPermissions("plugin-2");

        // Then: 应该返回正确的权限配置
        assertTrue(retrieved1.isAllowFileRead());
        assertFalse(retrieved1.isAllowNetwork());

        assertTrue(retrieved2.isAllowFileRead());
        assertTrue(retrieved2.isAllowNetwork());
        log.info("测试通过: 权限配置正确存储和检索");
    }
}
