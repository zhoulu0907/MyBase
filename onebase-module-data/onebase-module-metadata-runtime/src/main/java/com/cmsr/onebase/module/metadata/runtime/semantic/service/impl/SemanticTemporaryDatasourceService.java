package com.cmsr.onebase.module.metadata.runtime.semantic.service.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.datasource.DataSourceKey;

@Service
@Slf4j
public class SemanticTemporaryDatasourceService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, AnylineService<?>> serviceCache = new ConcurrentHashMap<>();
    private final Map<String, DataSource> datasourceCache = new ConcurrentHashMap<>();

    public AnylineService<?> createTemporaryService(MetadataDatasourceDO datasource) {
        Map<String, Object> config = parseAndNormalizeConfig(datasource.getConfig());
        config.put("datasourceType", datasource.getDatasourceType());
        return createTemporaryService(config);
    }

    public synchronized AnylineService<?> createTemporaryService(Map<String, Object> datasourceConfig) {
        String cacheKey = generateCacheKey(datasourceConfig);

        AnylineService<?> cachedService = serviceCache.get(cacheKey);
        DataSource cachedDataSource = datasourceCache.get(cacheKey);
        if (cachedService != null && cachedDataSource != null) {
            if (isDataSourceActive(cachedDataSource)) {
                log.debug("从缓存中获取临时数据源服务: {}", cacheKey);
                trySwitchMybatisFlex(cacheKey, cachedDataSource);
                return cachedService;
            } else {
                log.warn("缓存的数据源连接池已关闭，移除缓存并重新创建: {}", cacheKey);
                serviceCache.remove(cacheKey);
                datasourceCache.remove(cacheKey);
            }
        }

        try {
            String url = (String) datasourceConfig.get("url");
            String username = (String) datasourceConfig.get("username");
            String password = (String) datasourceConfig.get("password");
            String datasourceType = (String) datasourceConfig.get("datasourceType");

            if (url == null || url.trim().isEmpty()) {
                String host = (String) datasourceConfig.get("host");
                Object portObj = datasourceConfig.get("port");
                String database = (String) datasourceConfig.get("database");
                if (host != null && !host.trim().isEmpty()) {
                    if (portObj == null) { throw new IllegalArgumentException("数据源配置缺少必填参数：port"); }
                    int port;
                    if (portObj instanceof Integer) { port = (Integer) portObj; }
                    else if (portObj instanceof String) { port = Integer.parseInt((String) portObj); }
                    else { throw new IllegalArgumentException("端口号类型错误，应为Integer或String: " + portObj.getClass()); }
                    url = buildJdbcUrl(datasourceType, host, port, database);
                }
            }

            if (url == null || url.trim().isEmpty()) { throw new RuntimeException("无法构建数据源连接URL，请检查配置信息"); }

            log.info("=== 创建临时数据源调试信息 ===");
            log.info("构建的JDBC URL: {}", url);
            log.info("用户名: {}", username);
            log.info("数据库类型: {}", datasourceType);

            Map<String, Object> dsConfig = new HashMap<>();
            dsConfig.put("url", url);
            dsConfig.put("username", username != null ? username : "");
            dsConfig.put("password", password != null ? password : "");
            dsConfig.put("driver", getDriverByType(datasourceType));
            dsConfig.put("type", "com.zaxxer.hikari.HikariDataSource");
            dsConfig.put("minimum-idle", 1);
            dsConfig.put("maximum-pool-size", 5);
            dsConfig.put("connection-timeout", 30000);
            dsConfig.put("idle-timeout", 300000);
            dsConfig.put("max-lifetime", 1800000);
            dsConfig.put("leak-detection-threshold", 60000);
            dsConfig.put("connection-test-query", getConnectionTestQuery(datasourceType));
            dsConfig.put("validation-timeout", 5000);

            log.info("临时数据源配置: {}", dsConfig);

            DataSource dataSource = null;
            try {
                log.info("开始创建数据源，URL: {}", url);
                dataSource = DataSourceUtil.build(dsConfig);

                try (Connection testConn = dataSource.getConnection()) {
                    if (testConn == null || testConn.isClosed()) { throw new RuntimeException("创建的数据库连接无效"); }
                    try (Statement stmt = testConn.createStatement();
                         ResultSet rs = stmt.executeQuery(getConnectionTestQuery(datasourceType))) {
                        if (!rs.next()) { throw new RuntimeException("数据库连接测试查询失败"); }
                    }
                    log.info("数据源连接测试成功");
                }

                AnylineService<?> service = ServiceProxy.temporary(dataSource);

                serviceCache.put(cacheKey, service);
                datasourceCache.put(cacheKey, dataSource);

                trySwitchMybatisFlex(cacheKey, dataSource);

                log.info("临时服务创建成功，Service实例: {}", service.getClass().getName());
                log.info("=== 创建临时数据源调试信息结束 ===");

                return service;
            } catch (Exception e) {
                log.error("数据源连接创建或测试失败，URL: {}, 错误: {}", url, e.getMessage());
                if (dataSource != null) {
                    try {
                        if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                            ((com.zaxxer.hikari.HikariDataSource) dataSource).close();
                        }
                    } catch (Exception closeEx) {
                        log.warn("关闭失败的数据源时发生异常: {}", closeEx.getMessage());
                    }
                }
                String errorMsg = e.getMessage();
                if (errorMsg != null) {
                    if (errorMsg.contains("Connection refused") ||
                        errorMsg.contains("timeout") ||
                        errorMsg.contains("Network is unreachable") ||
                        errorMsg.contains("No route to host") ||
                        errorMsg.contains("connect timed out")) {
                        throw new RuntimeException("数据源网络不可达或连接超时，请检查数据源配置: " + errorMsg, e);
                    }
                    if (errorMsg.contains("HikariDataSource") && errorMsg.contains("has been closed")) {
                        throw new RuntimeException("数据源连接池已关闭，请重试或检查连接池配置: " + errorMsg, e);
                    }
                }
                throw e;
            }
        } catch (Exception e) {
            log.error("创建数据库连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage(), e);
        }
    }

    private void trySwitchMybatisFlex(String key, DataSource dataSource) {
        try {
            FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
            if (config == null) { return; }
            FlexDataSource flexDataSource = config.getDataSource();
            if (flexDataSource == null) { return; }
            try {
                flexDataSource.addDataSource(key, dataSource);
            } catch (Exception ignore) {
            }
            DataSourceKey.use(key);
            log.info("MyBatis-Flex 已切换到数据源: {}", key);
        } catch (Exception e) {
            log.warn("切换 MyBatis-Flex 数据源失败: {}", e.getMessage());
        }
    }

    private boolean isDataSourceActive(DataSource dataSource) {
        if (dataSource == null) { return false; }
        try {
            if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                com.zaxxer.hikari.HikariDataSource hikariDataSource = (com.zaxxer.hikari.HikariDataSource) dataSource;
                if (hikariDataSource.isClosed()) { return false; }
            }
            try (Connection testConn = dataSource.getConnection()) {
                return testConn != null && !testConn.isClosed();
            }
        } catch (Exception e) {
            log.debug("数据源活跃性检查失败: {}", e.getMessage());
            return false;
        }
    }

    public synchronized void cleanupInactiveDataSources() {
        log.debug("开始清理失效的数据源缓存");
        Set<String> keysToRemove = new HashSet<>();
        for (Map.Entry<String, DataSource> entry : datasourceCache.entrySet()) {
            String key = entry.getKey();
            DataSource dataSource = entry.getValue();
            if (!isDataSourceActive(dataSource)) {
                keysToRemove.add(key);
                try {
                    if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                        ((com.zaxxer.hikari.HikariDataSource) dataSource).close();
                    }
                } catch (Exception e) {
                    log.warn("关闭失效数据源时发生异常: {}", e.getMessage());
                }
            }
        }
        for (String key : keysToRemove) {
            serviceCache.remove(key);
            datasourceCache.remove(key);
            log.debug("移除失效的数据源缓存: {}", key);
        }
        log.debug("数据源缓存清理完成，移除了 {} 个失效项", keysToRemove.size());
    }

    private String generateCacheKey(Map<String, Object> datasourceConfig) {
        String url = (String) datasourceConfig.get("url");
        String username = (String) datasourceConfig.get("username");
        String datasourceType = (String) datasourceConfig.get("datasourceType");

        if (url == null) {
            String host = (String) datasourceConfig.get("host");
            Object portObj = datasourceConfig.get("port");
            String database = (String) datasourceConfig.get("database");
            if (host != null) {
                if (portObj == null) { throw new IllegalArgumentException("数据源配置缺少必填参数：port"); }
                int port;
                if (portObj instanceof Integer) { port = (Integer) portObj; }
                else if (portObj instanceof String) { port = Integer.parseInt((String) portObj); }
                else { throw new IllegalArgumentException("端口号类型错误，应为Integer或String: " + portObj.getClass()); }
                url = buildJdbcUrl(datasourceType, host, port, database);
            }
        }

        return String.format("temp_datasource_%s_%s_%s",
            datasourceType != null ? datasourceType : "unknown",
            url != null ? url.hashCode() : "nourl",
            username != null ? username.hashCode() : "nouser");
    }

    private Map<String, Object> parseAndNormalizeConfig(String configStr) {
        Map<String, Object> result = new HashMap<>();
        if (configStr == null || configStr.trim().isEmpty()) { return result; }
        String trimmed = configStr.trim();
        try {
            if (trimmed.matches("^[0-9,]+$")) {
                String[] arr = trimmed.split(",");
                byte[] bytes = new byte[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    try { bytes[i] = (byte) Integer.parseInt(arr[i]); }
                    catch (NumberFormatException ignore) {}
                }
                trimmed = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            }
            Map<String, Object> parsed = objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
            if (parsed != null) { result.putAll(parsed); }
        } catch (Exception e) {
            log.warn("数据源配置解析失败，返回空配置，并继续后续构建. raw={} error={}", abbreviate(configStr, 256), e.getMessage());
        }
        normalizeUserKey(result);
        normalizeUrlKeys(result);
        trimEmptyValues(result);
        return result;
    }

    private void normalizeUserKey(Map<String, Object> map) {
        if (!map.containsKey("username")) {
            Object user = map.get("user");
            if (user != null) { map.put("username", String.valueOf(user)); }
        }
    }

    private void normalizeUrlKeys(Map<String, Object> map) {
        String url = str(map.get("url"));
        String jdbcUrl = str(map.get("jdbcUrl"));
        if (StringUtils.hasText(jdbcUrl) && !StringUtils.hasText(url)) { map.put("url", jdbcUrl); }
        else if (StringUtils.hasText(url) && !StringUtils.hasText(jdbcUrl)) { map.put("jdbcUrl", url); }
    }

    private void trimEmptyValues(Map<String, Object> map) {
        map.replaceAll((k, v) -> v instanceof String ? ((String) v).trim() : v);
    }

    private String str(Object obj) { return obj == null ? null : String.valueOf(obj).trim(); }

    private String abbreviate(String text, int max) {
        if (text == null) return null;
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(0, max - 3)) + "...";
    }

    public void executeDDL(Map<String, Object> datasourceConfig, String ddl) {
        try {
            AnylineService<?> temporaryService = createTemporaryService(datasourceConfig);
            temporaryService.execute(ddl);
            log.info("成功执行DDL: {}", ddl);
        } catch (Exception e) {
            log.error("执行DDL失败: {}", ddl, e);
            throw new RuntimeException("执行DDL失败: " + e.getMessage(), e);
        }
    }

    public String buildJdbcUrl(String datasourceType, String host, int port, String database) {
        if (host == null || host.trim().isEmpty()) { throw new IllegalArgumentException("主机地址不能为空"); }
        if (port <= 0 || port > 65535) { throw new IllegalArgumentException("端口号必须在1-65535之间，当前值: " + port); }
        DatabaseType dbType;
        try { dbType = DatabaseType.valueOf(datasourceType); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("不支持的数据源类型: " + datasourceType); }
        String urlTemplate = dbType.url();
        String databasePart = (database != null && !database.trim().isEmpty()) ? database : "";
        String url = urlTemplate
                .replace("{host}", host)
                .replace("{database}", databasePart)
                .replace("{database/schema}", databasePart);
        url = url.replaceAll("\\{port:\\d+\\}", String.valueOf(port));
        return url;
    }

    public String getDriverByType(String datasourceType) {
        DatabaseType dbType = DatabaseType.valueOf(datasourceType);
        return dbType.driver();
    }

    private String getConnectionTestQuery(String datasourceType) {
        try {
            DatabaseType dbType = DatabaseType.valueOf(datasourceType);
            switch (dbType) {
                case DM:
                case ORACLE:
                    return "SELECT 1 FROM DUAL";
                case PostgreSQL:
                case KingBase:
                case MySQL:
                default:
                    return "SELECT 1";
            }
        } catch (IllegalArgumentException e) {
            log.warn("无法识别的数据库类型[{}]，使用默认测试查询SELECT 1", datasourceType);
            return "SELECT 1";
        }
    }
}
