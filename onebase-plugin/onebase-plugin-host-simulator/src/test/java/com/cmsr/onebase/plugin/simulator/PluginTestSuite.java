package com.cmsr.onebase.plugin.simulator;

import com.cmsr.onebase.plugin.simulator.config.ContextFileWatcherTest;
import com.cmsr.onebase.plugin.simulator.config.MockPluginContextServiceTest;
import com.cmsr.onebase.plugin.simulator.test.api.ContextDemoApiTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 插件综合测试套件
 * <p>
 * 包含 simulator 模块的所有测试用例：
 * 1. 核心配置测试 (Config)
 * 2. API 接口测试 (API)
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-10
 */
@Suite
@SelectClasses({
                // Configuration Tests
                ContextFileWatcherTest.class,
                MockPluginContextServiceTest.class,

                // API Tests
                ContextDemoApiTest.class
})
public class PluginTestSuite {
}
