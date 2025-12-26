package com.cmsr.onebase.plugin.runtime.test.util;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 插件状态断言工具
 * <p>
 * 提供链式 API 用于断言插件状态，简化测试代码。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
public class PluginStatusAssert {

    private static final Logger log = LoggerFactory.getLogger(PluginStatusAssert.class);

    private final OneBasePluginManager pluginManager;
    private final String pluginId;

    public PluginStatusAssert(OneBasePluginManager pluginManager, String pluginId) {
        this.pluginManager = pluginManager;
        this.pluginId = pluginId;
    }

    /**
     * 创建断言实例
     */
    public static PluginStatusAssert assertPlugin(OneBasePluginManager pluginManager, String pluginId) {
        return new PluginStatusAssert(pluginManager, pluginId);
    }

    /**
     * 断言插件存在
     */
    public PluginStatusAssert exists() {
        Optional<PluginWrapper> plugin = pluginManager.getPlugin(pluginId);
        assertThat(plugin).as("Plugin %s should exist", pluginId).isPresent();
        log.debug("✓ Plugin {} exists", pluginId);
        return this;
    }

    /**
     * 断言插件不存在
     */
    public PluginStatusAssert notExists() {
        Optional<PluginWrapper> plugin = pluginManager.getPlugin(pluginId);
        assertThat(plugin).as("Plugin %s should not exist", pluginId).isEmpty();
        log.debug("✓ Plugin {} does not exist", pluginId);
        return this;
    }

    /**
     * 断言插件状态
     */
    public PluginStatusAssert hasState(PluginState expectedState) {
        PluginState actualState = pluginManager.getPluginState(pluginId);
        assertThat(actualState)
                .as("Plugin %s state should be %s", pluginId, expectedState)
                .isEqualTo(expectedState);
        log.debug("✓ Plugin {} has state {}", pluginId, expectedState);
        return this;
    }

    /**
     * 断言插件已启动
     */
    public PluginStatusAssert isStarted() {
        return hasState(PluginState.STARTED);
    }

    /**
     * 断言插件已停止
     */
    public PluginStatusAssert isStopped() {
        return hasState(PluginState.STOPPED);
    }

    /**
     * 断言插件已加载但未启动
     */
    public PluginStatusAssert isLoaded() {
        PluginState state = pluginManager.getPluginState(pluginId);
        assertThat(state)
                .as("Plugin %s should be loaded (CREATED or RESOLVED)", pluginId)
                .isIn(PluginState.CREATED, PluginState.RESOLVED);
        log.debug("✓ Plugin {} is loaded but not started (state: {})", pluginId, state);
        return this;
    }

    /**
     * 断言插件在已启动列表中
     */
    public PluginStatusAssert isInStartedList() {
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        boolean found = startedPlugins.stream()
                .anyMatch(p -> p.getPluginId().equals(pluginId));
        assertThat(found)
                .as("Plugin %s should be in started plugins list", pluginId)
                .isTrue();
        log.debug("✓ Plugin {} is in started plugins list", pluginId);
        return this;
    }

    /**
     * 断言插件不在已启动列表中
     */
    public PluginStatusAssert isNotInStartedList() {
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        boolean found = startedPlugins.stream()
                .anyMatch(p -> p.getPluginId().equals(pluginId));
        assertThat(found)
                .as("Plugin %s should not be in started plugins list", pluginId)
                .isFalse();
        log.debug("✓ Plugin {} is not in started plugins list", pluginId);
        return this;
    }

    /**
     * 断言插件未启动
     */
    public PluginStatusAssert isNotStarted() {
        PluginState state = pluginManager.getPluginState(pluginId);
        assertThat(state)
                .as("Plugin %s should not be STARTED", pluginId)
                .isNotEqualTo(PluginState.STARTED);
        log.debug("✓ Plugin {} is not started (state: {})", pluginId, state);
        return this;
    }

    /**
     * 断言插件有指定数量的 HttpHandler
     */
    public PluginStatusAssert hasHttpHandlerCount(int expectedCount) {
        int actualCount = pluginManager.getHttpHandlers(pluginId).size();
        assertThat(actualCount)
                .as("Plugin %s should have %d HttpHandlers", pluginId, expectedCount)
                .isEqualTo(expectedCount);
        log.debug("✓ Plugin {} has {} HttpHandlers", pluginId, expectedCount);
        return this;
    }

    /**
     * 断言插件版本
     */
    public PluginStatusAssert hasVersion(String expectedVersion) {
        Optional<PluginWrapper> plugin = pluginManager.getPlugin(pluginId);
        assertThat(plugin).as("Plugin %s should exist", pluginId).isPresent();

        String actualVersion = plugin.get().getDescriptor().getVersion();
        assertThat(actualVersion)
                .as("Plugin %s version should be %s", pluginId, expectedVersion)
                .isEqualTo(expectedVersion);
        log.debug("✓ Plugin {} has version {}", pluginId, expectedVersion);
        return this;
    }

    /**
     * 断言插件描述
     */
    public PluginStatusAssert hasDescription(String expectedDescription) {
        Optional<PluginWrapper> plugin = pluginManager.getPlugin(pluginId);
        assertThat(plugin).as("Plugin %s should exist", pluginId).isPresent();

        String actualDescription = plugin.get().getDescriptor().getPluginDescription();
        assertThat(actualDescription)
                .as("Plugin %s description should be %s", pluginId, expectedDescription)
                .isEqualTo(expectedDescription);
        log.debug("✓ Plugin {} has description: {}", pluginId, expectedDescription);
        return this;
    }

    /**
     * 获取插件状态（用于自定义断言）
     */
    public PluginState getState() {
        return pluginManager.getPluginState(pluginId);
    }

    /**
     * 获取插件包装器（用于自定义断言）
     */
    public Optional<PluginWrapper> getPlugin() {
        return pluginManager.getPlugin(pluginId);
    }
}
