package com.cmsr.onebase.plugin.simulator.test.util;

/**
 * ContextDemoController 测试数据构建器
 * <p>
 * 提供 ContextDemoController 相关测试的数据和字段验证
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-10
 */
public class ContextDemoData {

    public static String sampleConfigKey() {
        return "apiKey";
    }

    public static String[] contextAllResponseFields() {
        return new String[] { "pluginId", "pluginVersion", "config", "timestamp" };
    }

    public static String[] contextTenantIdResponseFields() {
        return new String[] { "tenantId", "timestamp" };
    }

    public static String[] contextKeyValueResponseFields() {
        return new String[] { "key", "value", "exists", "timestamp" };
    }

    public static String[] contextDemoResponseFields() {
        return new String[] { "message", "provider", "apiKeyConfigured", "timeout", "maxRetries", "logEnabled" };
    }

    public static String[] contextInfoResponseFields() {
        return new String[] { "pluginId", "pluginVersion", "configCount", "configKeys", "timestamp", "apiTips" };
    }
}
