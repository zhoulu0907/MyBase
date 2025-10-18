package com.cmsr.onebase.framework.dolphins;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * DolphinScheduler 模块聚合测试套件
 *
 * 一键运行本套件可同时执行该模块下的所有核心用例，
 * 包含 API 连通性、序列化兼容性、实例控制等测试。
 *
 * 使用方式：
 * - IDE：右键运行本类（Run AllDolphinSchedulerSuite）
 * - Maven：mvn -pl onebase-framework/onebase-spring-boot-starter-dolphinscheduler -am -Dtest=AllDolphinSchedulerSuite test
 *
 * 注意：部分集成测试依赖外部 DolphinScheduler 服务；
 *       若网络/环境不可达，这些用例会通过 Assumptions 自动跳过，不影响整体结果。
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Suite
@SelectClasses({
        ProjectApiTest.class,
        WorkflowApiTest.class,
        ScheduleApiTest.class,
        TaskApiTest.class,
        TaskInstanceApiTest.class,
        WorkflowInstanceApiTest.class,
        ScheduleEnumSerializationTest.class
})
public class AllDolphinSchedulerSuite {
}
