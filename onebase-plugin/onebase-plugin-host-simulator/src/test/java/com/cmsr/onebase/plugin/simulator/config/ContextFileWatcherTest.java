package com.cmsr.onebase.plugin.simulator.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置文件热加载测试
 *
 * @date 2025-01-08
 */
public class ContextFileWatcherTest {

    @TempDir
    Path tempDir;

    private Path configFile;
    private ContextFileWatcher watcher;
    private AtomicInteger reloadCount;
    private CountDownLatch reloadLatch;

    @BeforeEach
    void setUp() throws IOException {
        // 创建临时配置文件
        configFile = tempDir.resolve("plugin-context.yml");
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value1\n");

        reloadCount = new AtomicInteger(0);
    }

    @AfterEach
    void tearDown() {
        if (watcher != null) {
            watcher.stop();
        }
    }

    @Test
    void testConfigFileChange() throws Exception {
        // 准备：创建监听器，期待触发一次重载
        reloadLatch = new CountDownLatch(1);
        watcher = new ContextFileWatcher(configFile, () -> {
            reloadCount.incrementAndGet();
            reloadLatch.countDown();
        });
        watcher.start();

        // 等待监听器启动
        Thread.sleep(200);

        // 执行：修改配置文件
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value2\n");

        // 验证：等待重载触发（最多等待2秒）
        boolean reloaded = reloadLatch.await(2, TimeUnit.SECONDS);
        assertTrue(reloaded, "配置重载应该被触发");
        assertEquals(1, reloadCount.get(), "应该触发一次重载");
    }

    @Test
    void testDebounce() throws Exception {
        // 准备：创建监听器，期待多次修改只触发一次重载
        reloadLatch = new CountDownLatch(1);
        watcher = new ContextFileWatcher(configFile, () -> {
            reloadCount.incrementAndGet();
            reloadLatch.countDown();
        });
        watcher.start();

        // 等待监听器启动
        Thread.sleep(200);

        // 执行：短时间内多次修改文件
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value2\n");
        Thread.sleep(100);
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value3\n");
        Thread.sleep(100);
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value4\n");

        // 验证：等待重载触发
        boolean reloaded = reloadLatch.await(2, TimeUnit.SECONDS);
        assertTrue(reloaded, "配置重载应该被触发");

        // 再等待一段时间，确保没有额外的重载
        Thread.sleep(1000);
        assertEquals(1, reloadCount.get(), "防抖机制应该合并多次修改为一次重载");
    }

    @Test
    void testStopWatcher() throws Exception {
        // 准备：创建并启动监听器
        reloadLatch = new CountDownLatch(1);
        watcher = new ContextFileWatcher(configFile, () -> {
            reloadCount.incrementAndGet();
            reloadLatch.countDown();
        });
        watcher.start();
        Thread.sleep(200);

        // 执行：停止监听器
        watcher.stop();
        Thread.sleep(200);

        // 修改文件
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value2\n");

        // 验证：不应该触发重载
        boolean reloaded = reloadLatch.await(1, TimeUnit.SECONDS);
        assertFalse(reloaded, "停止后不应该触发重载");
        assertEquals(0, reloadCount.get(), "停止后修改文件不应该触发重载");
    }

    @Test
    void testNonExistentFile() {
        // 准备：使用不存在的文件路径
        Path nonExistentFile = tempDir.resolve("non-existent.yml");

        // 执行：创建监听器
        watcher = new ContextFileWatcher(nonExistentFile, () -> reloadCount.incrementAndGet());
        watcher.start();

        // 验证：应该优雅地处理，不抛出异常
        assertEquals(0, reloadCount.get(), "不存在的文件不应该触发重载");
    }

    @Test
    void testReloadCallbackException() throws Exception {
        // 准备：创建会抛出异常的回调
        reloadLatch = new CountDownLatch(1);
        watcher = new ContextFileWatcher(configFile, () -> {
            reloadCount.incrementAndGet();
            reloadLatch.countDown();
            throw new RuntimeException("Test exception");
        });
        watcher.start();
        Thread.sleep(200);

        // 执行：修改文件
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value2\n");

        // 验证：即使回调抛出异常，监听器应该继续工作
        boolean reloaded = reloadLatch.await(2, TimeUnit.SECONDS);
        assertTrue(reloaded, "即使回调抛出异常，也应该触发重载");
        assertEquals(1, reloadCount.get());

        // 再次修改，验证监听器仍然工作
        reloadLatch = new CountDownLatch(1);
        Files.writeString(configFile, "plugins:\n  test-plugin:\n    key1: value3\n");
        reloaded = reloadLatch.await(2, TimeUnit.SECONDS);
        assertTrue(reloaded, "监听器应该在异常后继续工作");
    }
}
