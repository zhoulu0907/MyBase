package com.cmsr.onebase.plugin.runtime.test;

import com.cmsr.onebase.plugin.runtime.test.api.DevModeApiTest;
import com.cmsr.onebase.plugin.runtime.test.api.DevModeAutoLoadFalseAutoStartFalseTest;
import com.cmsr.onebase.plugin.runtime.test.api.DevModeAutoLoadFalseAutoStartTrueTest;
import com.cmsr.onebase.plugin.runtime.test.api.DevModeAutoLoadTrueAutoStartFalseTest;
import com.cmsr.onebase.plugin.runtime.test.api.DevModeClassPathsTest;
import com.cmsr.onebase.plugin.runtime.test.api.ProdModeApiTest;
import com.cmsr.onebase.plugin.runtime.test.api.StagingModeApiTest;
import com.cmsr.onebase.plugin.runtime.test.exception.PluginExceptionScenariosTest;
import com.cmsr.onebase.plugin.runtime.test.integration.PluginSystemIntegrationTest;
import com.cmsr.onebase.plugin.runtime.test.lifecycle.PluginLifecycleTest;
import com.cmsr.onebase.plugin.runtime.test.reload.DevModeHotReloadTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * 插件系统综合测试套件
 * <p>
 * 聚合所有测试类，可以在 IDEA 中一键运行所有测试
 * </p>
 * <p>
 * 包含的测试类：
 * <ul>
 * <li>集成测试（1个类，2个用例）</li>
 * <li>API 测试（7个类，47个用例）</li>
 * <li>生命周期测试（1个类，4个用例）</li>
 * <li>热重载测试（1个类，3个用例）</li>
 * </ul>
 * <p>
 * 注：原 config 包的配置测试已合并到 api 包，避免重复测试。
 * </p>
 * <p>
 * 使用方法：
 * <ul>
 * <li>在 IDEA 中右键点击此类，选择 "Run 'PluginTestSuite'"</li>
 * <li>或使用命令行：mvn test -Dtest=PluginTestSuite</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
@Suite
@SuiteDisplayName("插件系统综合测试套件")
@SelectClasses({
        // 集成冒烟测试
        PluginSystemIntegrationTest.class,

        // API 响应测试（包含配置验证）
        DevModeApiTest.class,
        DevModeAutoLoadTrueAutoStartFalseTest.class,
        DevModeAutoLoadFalseAutoStartFalseTest.class,
        DevModeAutoLoadFalseAutoStartTrueTest.class,
        DevModeClassPathsTest.class,
        StagingModeApiTest.class,
        ProdModeApiTest.class,

        // 生命周期测试
        PluginLifecycleTest.class,

        // 热重载测试
        DevModeHotReloadTest.class,

        // 插件异常场景测试
        PluginExceptionScenariosTest.class
})
public class PluginTestSuite {
    // 这个类不需要任何代码
    // JUnit 5 会自动运行 @SelectClasses 中指定的所有测试类
}
