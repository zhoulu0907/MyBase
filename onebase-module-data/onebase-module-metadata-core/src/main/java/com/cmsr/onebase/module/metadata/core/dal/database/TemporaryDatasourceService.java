package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 临时数据源服务，用于动态创建和管理AnylineService实例
 * 该服务主要用于元数据模块中对不同数据源的动态访问
 *
 * @author bty418
 * @date 2025-01-25
 */
@Component
@Slf4j
public class TemporaryDatasourceService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 用于缓存已创建的临时服务，避免重复创建
     */
    private final Map<String, AnylineService<?>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 根据数据源DO对象创建临时的AnylineService用于数据库操作
     *
     * @param datasource 数据源配置对象
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(MetadataDatasourceDO datasource) {
        // 从数据源配置中获取连接参数
    Map<String, Object> config = parseAndNormalizeConfig(datasource.getConfig());
    config.put("datasourceType", datasource.getDatasourceType());

        // 使用本助手的 Map 版本创建临时服务
        return createTemporaryService(config);
    }

    /**
     * 根据数据源配置参数创建临时 AnylineService 服务
     *
     * @param datasourceConfig 数据源配置参数
     * @return AnylineService 实例
     */
    public synchronized AnylineService<?> createTemporaryService(Map<String, Object> datasourceConfig) {
        // 生成数据源的唯一标识，用于缓存
        String cacheKey = generateCacheKey(datasourceConfig);

        // 先从缓存中获取
        AnylineService<?> cachedService = serviceCache.get(cacheKey);
        if (cachedService != null) {
            log.debug("从缓存中获取临时数据源服务: {}", cacheKey);
            return cachedService;
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
                    int port = getDefaultPort(datasourceType);
                    if (portObj instanceof Integer) {
                        port = (Integer) portObj;
                    } else if (portObj instanceof String) {
                        port = Integer.parseInt((String) portObj);
                    }
                    url = buildJdbcUrl(datasourceType, host, port, database);
                }
            }

            if (url == null || url.trim().isEmpty()) {
                throw new RuntimeException("无法构建数据源连接URL，请检查配置信息");
            }

            log.info("=== 创建临时数据源调试信息 ===");
            log.info("构建的JDBC URL: {}", url);
            log.info("用户名: {}", username);
            log.info("数据库类型: {}", datasourceType);
            // 创建数据源配置 - 使用com.zaxxer.hikari.HikariDataSource连接池
            Map<String, Object> dsConfig = new HashMap<>();
            dsConfig.put("url", url);
            dsConfig.put("username", username != null ? username : "");
            dsConfig.put("password", password != null ? password : "");
            dsConfig.put("driver", getDriverByType(datasourceType));
            // 指定连接池类型，使用com.zaxxer.hikari.HikariDataSource
            dsConfig.put("pool", "com.zaxxer.hikari.HikariDataSource");
            // 添加com.zaxxer.hikari.HikariDataSource连接池配置
            dsConfig.put("initial-size", 1);
            dsConfig.put("max-active", 10);
            dsConfig.put("min-idle", 1);
            dsConfig.put("max-wait", 30000);
            dsConfig.put("validation-query", "SELECT 1");
            dsConfig.put("test-on-borrow", false);
            dsConfig.put("test-on-return", false);
            dsConfig.put("test-while-idle", true);
            dsConfig.put("time-between-eviction-runs-millis", 60000);

            // 关键配置：限制连接失败重试次数，避免无限重试死循环
            dsConfig.put("connection-error-retry-attempts", 5); // 连接失败重试次数上限为5次
            dsConfig.put("break-after-acquire-failure", true);  // 获取连接失败后中断，避免死循环
            dsConfig.put("connect-timeout", 10000);             // 连接超时时间10秒
            dsConfig.put("socket-timeout", 30000);              // Socket超时时间30秒
            dsConfig.put("fail-fast", true);                    // 快速失败，不无限等待

            log.info("临时数据源配置: {}", dsConfig);

            // 创建数据源和临时服务 - 增加超时控制和快速失败机制
            DataSource dataSource = null;
            try {
                log.info("开始创建数据源，URL: {}", url);
                dataSource = DataSourceUtil.build(dsConfig);

                // 立即测试连接有效性，避免延迟发现问题
                try (Connection testConn = dataSource.getConnection()) {
                    if (testConn == null || testConn.isClosed()) {
                        throw new RuntimeException("创建的数据库连接无效");
                    }
                    // 执行简单查询验证连接
                    try (Statement stmt = testConn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT 1")) {
                        if (!rs.next()) {
                            throw new RuntimeException("数据库连接测试查询失败");
                        }
                    }
                    log.info("数据源连接测试成功");
                }

                AnylineService<?> service = ServiceProxy.temporary(dataSource);

                // 缓存创建的服务
                serviceCache.put(cacheKey, service);

                log.info("临时服务创建成功，Service实例: {}", service.getClass().getName());
                log.info("=== 创建临时数据源调试信息结束 ===");

                return service;
            } catch (Exception e) {
                log.error("数据源连接创建或测试失败，URL: {}, 错误: {}", url, e.getMessage());

                // 如果是连接超时或网络不可达，快速抛出异常
                String errorMsg = e.getMessage();
                if (errorMsg != null) {
                    if (errorMsg.contains("Connection refused") ||
                        errorMsg.contains("timeout") ||
                        errorMsg.contains("Network is unreachable") ||
                        errorMsg.contains("No route to host") ||
                        errorMsg.contains("connect timed out")) {
                        throw new RuntimeException("数据源网络不可达或连接超时，请检查数据源配置: " + errorMsg, e);
                    }
                }
                throw e; // 其他异常直接抛出
            }
        } catch (Exception e) {
            log.error("创建数据库连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成数据源配置的唯一缓存键
     *
     * @param datasourceConfig 数据源配置
     * @return 缓存键
     */
    private String generateCacheKey(Map<String, Object> datasourceConfig) {
        String url = (String) datasourceConfig.get("url");
        String username = (String) datasourceConfig.get("username");
        String datasourceType = (String) datasourceConfig.get("datasourceType");

        if (url == null) {
            String host = (String) datasourceConfig.get("host");
            Object portObj = datasourceConfig.get("port");
            String database = (String) datasourceConfig.get("database");
            if (host != null) {
                int port = getDefaultPort(datasourceType);
                if (portObj instanceof Integer) {
                    port = (Integer) portObj;
                } else if (portObj instanceof String) {
                    port = Integer.parseInt((String) portObj);
                }
                url = buildJdbcUrl(datasourceType, host, port, database);
            }
        }

        return String.format("temp_datasource_%s_%s_%s",
            datasourceType != null ? datasourceType : "unknown",
            url != null ? url.hashCode() : "nourl",
            username != null ? username.hashCode() : "nouser");
    }

    /**
     * 解析并规范化数据源配置 JSON 字符串
     * <p>
     * 支持两类输入：
     * 1) 标准 JSON 字符串 {"url":"jdbc:...", "username":"..."}
     * 2) 旧格式的数字逗号分隔串(来源于序列化字节数组) 例如: 123,34,117,114,108 ...
     * <p>
     * 规范化处理包含：
     * - user -> username 映射
     * - jdbcUrl -> url, 或 url -> jdbcUrl 双向补齐
     * - 去除可能的首尾空白
     * - 忽略空字符串字段
     *
     * @param configStr 原始配置字符串
     * @return 规范化后的 Map(永不为 null)
     */
    private Map<String, Object> parseAndNormalizeConfig(String configStr) {
        Map<String, Object> result = new HashMap<>();
        if (configStr == null || configStr.trim().isEmpty()) {
            return result;
        }
        String trimmed = configStr.trim();
        try {
            // 数字串形式(可能是 byte 数组序列化) 判断: 只包含数字与逗号
            if (trimmed.matches("^[0-9,]+$")) {
                String[] arr = trimmed.split(",");
                byte[] bytes = new byte[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    try {
                        bytes[i] = (byte) Integer.parseInt(arr[i]);
                    } catch (NumberFormatException ignore) {
                        // 保留 0 值即可
                    }
                }
                trimmed = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            }
            // JSON 解析
            Map<String, Object> parsed = objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
            if (parsed != null) {
                result.putAll(parsed);
            }
        } catch (Exception e) {
            log.warn("数据源配置解析失败，返回空配置，并继续后续构建. raw={} error={}", abbreviate(configStr, 256), e.getMessage());
        }

        // 统一 key 规范化(忽略大小写的 user -> username)
        normalizeUserKey(result);
        normalizeUrlKeys(result);
        trimEmptyValues(result);
        return result;
    }

    /**
     * user -> username
     */
    private void normalizeUserKey(Map<String, Object> map) {
        if (!map.containsKey("username")) {
            Object user = map.get("user");
            if (user != null) {
                map.put("username", String.valueOf(user));
            }
        }
    }

    /**
     * 处理 jdbcUrl 与 url 双向补齐, 优先采用已有的非空值
     */
    private void normalizeUrlKeys(Map<String, Object> map) {
        String url = str(map.get("url"));
        String jdbcUrl = str(map.get("jdbcUrl"));
        if (StringUtils.hasText(jdbcUrl) && !StringUtils.hasText(url)) {
            map.put("url", jdbcUrl);
        } else if (StringUtils.hasText(url) && !StringUtils.hasText(jdbcUrl)) {
            map.put("jdbcUrl", url);
        }
    }

    /**
     * 去除值为纯空白串的字段
     */
    private void trimEmptyValues(Map<String, Object> map) {
        map.replaceAll((k, v) -> v instanceof String ? ((String) v).trim() : v);
        // 不移除 key, 防止后续逻辑依赖; 仅保留空串本身即可
    }

    private String str(Object obj) {
        return obj == null ? null : String.valueOf(obj).trim();
    }

    /**
     * 文本过长重写省略
     */
    private String abbreviate(String text, int max) {
        if (text == null) return null;
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(0, max - 3)) + "...";
    }

    /**
     * 执行 DDL 语句
     *
     * @param datasourceConfig 数据源配置参数
     * @param ddl DDL 语句
     */
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

    /**
     * 根据数据源类型构建 JDBC URL
     */
    public String buildJdbcUrl(String datasourceType, String host, int port, String database) {
        if (host == null || host.trim().isEmpty()) {
            throw new RuntimeException("主机地址不能为空");
        }
        String databasePart = (database != null && !database.trim().isEmpty()) ? database : "";
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, databasePart);
            case "MYSQL":
                return String.format(
                        "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
                        host, port, databasePart);
            case "ORACLE":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databasePart);
            case "SQLSERVER":
                return String.format("jdbc:sqlserver://%s:%d;DatabaseName=%s", host, port, databasePart);
            case "KINGBASE":
                return String.format("jdbc:kingbase8://%s:%d/%s", host, port, databasePart);
            case "TDENGINE":
                return String.format("jdbc:TAOS-RS://%s:%d/%s", host, port, databasePart);
            case "CLICKHOUSE":
                return String.format("jdbc:clickhouse://%s:%d/%s", host, port, databasePart);
            case "DM":
                return String.format("jdbc:dm://%s:%d/%s", host, port, databasePart);
            case "OPENGAUSS":
                return String.format("jdbc:opengauss://%s:%d/%s", host, port, databasePart);
            case "DB2":
                return String.format("jdbc:db2://%s:%d/%s", host, port, databasePart);
            default:
                log.warn("未知的数据源类型，使用通用格式: {}", datasourceType);
                return String.format("jdbc:%s://%s:%d/%s", datasourceType.toLowerCase(), host, port, databasePart);
        }
    }

    /**
     * 根据数据源类型获取驱动类名
     */
    public String getDriverByType(String datasourceType) {
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return "org.postgresql.Driver";
            case "MYSQL":
                return "com.mysql.cj.jdbc.Driver";
            case "ORACLE":
                return "oracle.jdbc.driver.OracleDriver";
            case "SQLSERVER":
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "KINGBASE":
                return "com.kingbase8.Driver";
            case "TDENGINE":
                return "com.taosdata.jdbc.TSDBDriver";
            case "CLICKHOUSE":
                return "ru.yandex.clickhouse.ClickHouseDriver";
            case "DM":
                return "dm.jdbc.driver.DmDriver";
            case "OPENGAUSS":
                return "org.opengauss.Driver";
            case "DB2":
                return "com.ibm.db2.jcc.DB2Driver";
            default:
                throw new RuntimeException("不支持的数据源类型: " + datasourceType);
        }
    }

    /**
     * 根据数据源类型获取默认端口
     */
    public int getDefaultPort(String datasourceType) {
        switch (datasourceType.toUpperCase()) {
            case "POSTGRESQL":
                return 5432;
            case "MYSQL":
                return 3306;
            case "ORACLE":
                return 1521;
            case "SQLSERVER":
                return 1433;
            case "KINGBASE":
                return 54321;
            case "TDENGINE":
                return 6041;
            case "CLICKHOUSE":
                return 8123;
            case "DM":
                return 5236;
            case "OPENGAUSS":
                return 5432;
            case "DB2":
                return 50000;
            default:
                return 5432;
        }
    }

}
