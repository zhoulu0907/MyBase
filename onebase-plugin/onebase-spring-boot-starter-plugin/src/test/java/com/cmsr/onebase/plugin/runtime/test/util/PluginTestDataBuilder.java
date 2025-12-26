package com.cmsr.onebase.plugin.runtime.test.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试数据构建器
 * <p>
 * 为各个测试接口提供标准的测试数据和预期响应模板。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
public class PluginTestDataBuilder {

    /**
     * HelloWorldHandler 测试数据
     */
    public static class HelloWorldData {

        public static Map<String, String> helloQueryParams(String name) {
            Map<String, String> params = new HashMap<>();
            if (name != null) {
                params.put("name", name);
            }
            return params;
        }

        public static Map<String, Object> processRequestBody(String name, int value) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("value", value);
            return data;
        }

        public static Map<String, Object> emptyProcessRequestBody() {
            return new HashMap<>();
        }
    }

    /**
     * HutoolCryptoHandler 测试数据
     */
    public static class HutoolCryptoData {

        public static Map<String, String> cryptoQueryParams(String text) {
            Map<String, String> params = new HashMap<>();
            if (text != null) {
                params.put("text", text);
            }
            return params;
        }

        public static String defaultText() {
            return "Hello OneBase Plugin";
        }

        public static String customText() {
            return "Test Encryption";
        }
    }

    /**
     * CYSTestController 测试数据
     */
    public static class CYSTestData {

        public static Map<String, String> cysinfoQueryParams(String name) {
            Map<String, String> params = new HashMap<>();
            if (name != null) {
                params.put("name", name);
            }
            return params;
        }
    }

    /**
     * CustomApiHandler 测试数据
     */
    public static class CustomApiData {

        public static Map<String, Object> processRequestBody(Map<String, Object> data) {
            return data != null ? data : new HashMap<>();
        }

        public static Map<String, Object> sampleProcessData() {
            Map<String, Object> data = new HashMap<>();
            data.put("key1", "value1");
            data.put("key2", 123);
            data.put("key3", true);
            return data;
        }

        public static Map<String, Object> emptyProcessData() {
            return new HashMap<>();
        }
    }

    /**
     * 插件管理 API 测试数据
     */
    public static class PluginManagementData {

        public static String pluginId() {
            return "hello-plugin";
        }

        public static String pluginZipPath() {
            return "plugins/hello-plugin-1.0.0.zip";
        }
    }

    /**
     * 预期响应字段验证器
     */
    public static class ExpectedFields {

        /**
         * HelloWorldHandler.hello 预期字段
         */
        public static String[] helloResponseFields() {
            return new String[] { "message", "timestamp", "plugin", "loadSource", "version" };
        }

        /**
         * HelloWorldHandler.process 预期字段
         */
        public static String[] processResponseFields() {
            return new String[] { "received", "size", "timestamp", "plugin", "message" };
        }

        /**
         * HutoolCryptoHandler.crypto 预期字段
         */
        public static String[] cryptoResponseFields() {
            return new String[] { "md5", "sha256", "aesEncrypted", "aesDecrypted",
                    "aesVerified", "originalText", "hutoolVersion",
                    "hutoolLoaded", "pluginId", "message", "success" };
        }

        /**
         * HutoolCryptoHandler.checkHutool 预期字段
         */
        public static String[] checkHutoolResponseFields() {
            return new String[] { "hutoolClassLoaded", "hutoolClassName", "classLoader",
                    "testMd5", "md5Correct", "message", "success" };
        }

        /**
         * CYSTestController.cysinfo 预期字段
         */
        public static String[] cysinfoResponseFields() {
            return new String[] { "message", "timestamp", "plugin" };
        }

        /**
         * CustomApiHandler.getPluginInfo 预期字段
         */
        public static String[] pluginInfoResponseFields() {
            return new String[] { "plugin", "version", "description", "features",
                    "springInjectionWorking" };
        }

        /**
         * CustomApiHandler.getStatus 预期字段
         */
        public static String[] statusResponseFields() {
            return new String[] { "status", "uptime" };
        }

        /**
         * CustomApiHandler.processData 预期字段
         */
        public static String[] customApiProcessResponseFields() {
            return new String[] { "success", "receivedData", "processedCount" };
        }
    }
}
