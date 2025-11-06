package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
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
     * 用于缓存数据源实例，便于管理连接池生命周期
     */
    private final Map<String, DataSource> datasourceCache = new ConcurrentHashMap<>();

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

        // 先从缓存中获取，但需要检查连接池状态
        AnylineService<?> cachedService = serviceCache.get(cacheKey);
        DataSource cachedDataSource = datasourceCache.get(cacheKey);
        
        if (cachedService != null && cachedDataSource != null) {
            // 检查连接池是否仍然活跃
            if (isDataSourceActive(cachedDataSource)) {
                log.debug("从缓存中获取临时数据源服务: {}", cacheKey);
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
                    // 端口号必填，不提供默认值
                    if (portObj == null) {
                        throw new IllegalArgumentException("数据源配置缺少必填参数：port");
                    }
                    int port;
                    if (portObj instanceof Integer) {
                        port = (Integer) portObj;
                    } else if (portObj instanceof String) {
                        port = Integer.parseInt((String) portObj);
                    } else {
                        throw new IllegalArgumentException("端口号类型错误，应为Integer或String: " + portObj.getClass());
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
            
            // 创建数据源配置 - 使用更稳定的HikariDataSource连接池配置
            Map<String, Object> dsConfig = new HashMap<>();
            dsConfig.put("url", url);
            dsConfig.put("username", username != null ? username : "");
            dsConfig.put("password", password != null ? password : "");
            dsConfig.put("driver", getDriverByType(datasourceType));
            // 指定连接池类型，使用com.zaxxer.hikari.HikariDataSource
            dsConfig.put("pool", "com.zaxxer.hikari.HikariDataSource");
            
            // 优化HikariCP连接池配置，提高稳定性
            dsConfig.put("minimum-idle", 1);              // 最小空闲连接数
            dsConfig.put("maximum-pool-size", 5);         // 最大连接池大小，避免过多连接
            dsConfig.put("connection-timeout", 30000);    // 连接超时30秒
            dsConfig.put("idle-timeout", 300000);         // 空闲超时5分钟
            dsConfig.put("max-lifetime", 1800000);        // 连接最大生命周期30分钟
            dsConfig.put("leak-detection-threshold", 60000); // 连接泄露检测阈值1分钟
            
            // 连接有效性检查配置 - 根据数据库类型使用不同的测试查询
            dsConfig.put("connection-test-query", getConnectionTestQuery(datasourceType));
            dsConfig.put("validation-timeout", 5000);     // 验证超时5秒
            
            // 移除可能导致问题的配置
            // dsConfig.put("fail-fast", true);          // 移除快速失败，避免过早关闭
            // dsConfig.put("break-after-acquire-failure", true); // 移除，避免获取失败后立即中断

            log.info("临时数据源配置: {}", dsConfig);

            // 创建数据源和临时服务 - 增强错误处理和连接验证
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
                         ResultSet rs = stmt.executeQuery(getConnectionTestQuery(datasourceType))) {
                        if (!rs.next()) {
                            throw new RuntimeException("数据库连接测试查询失败");
                        }
                    }
                    log.info("数据源连接测试成功");
                }

                AnylineService<?> service = ServiceProxy.temporary(dataSource);

                // 缓存创建的服务和数据源
                serviceCache.put(cacheKey, service);
                datasourceCache.put(cacheKey, dataSource);

                log.info("临时服务创建成功，Service实例: {}", service.getClass().getName());
                log.info("=== 创建临时数据源调试信息结束 ===");

                return service;
            } catch (Exception e) {
                log.error("数据源连接创建或测试失败，URL: {}, 错误: {}", url, e.getMessage());

                // 清理可能创建的资源
                if (dataSource != null) {
                    try {
                        if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                            ((com.zaxxer.hikari.HikariDataSource) dataSource).close();
                        }
                    } catch (Exception closeEx) {
                        log.warn("关闭失败的数据源时发生异常: {}", closeEx.getMessage());
                    }
                }

                // 如果是连接超时或网络不可达，提供更详细的错误信息
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
                throw e; // 其他异常直接抛出
            }
        } catch (Exception e) {
            log.error("创建数据库连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查数据源是否仍然活跃
     *
     * @param dataSource 数据源实例
     * @return 是否活跃
     */
    private boolean isDataSourceActive(DataSource dataSource) {
        if (dataSource == null) {
            return false;
        }
        
        try {
            // 检查HikariDataSource是否已关闭
            if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                com.zaxxer.hikari.HikariDataSource hikariDataSource = (com.zaxxer.hikari.HikariDataSource) dataSource;
                if (hikariDataSource.isClosed()) {
                    return false;
                }
            }
            
            // 尝试获取连接并快速测试
            try (Connection testConn = dataSource.getConnection()) {
                return testConn != null && !testConn.isClosed();
            }
        } catch (Exception e) {
            log.debug("数据源活跃性检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 清理缓存中的失效数据源
     */
    public synchronized void cleanupInactiveDataSources() {
        log.debug("开始清理失效的数据源缓存");
        Set<String> keysToRemove = new HashSet<>();
        
        for (Map.Entry<String, DataSource> entry : datasourceCache.entrySet()) {
            String key = entry.getKey();
            DataSource dataSource = entry.getValue();
            
            if (!isDataSourceActive(dataSource)) {
                keysToRemove.add(key);
                try {
                    // 尝试关闭失效的数据源
                    if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                        ((com.zaxxer.hikari.HikariDataSource) dataSource).close();
                    }
                } catch (Exception e) {
                    log.warn("关闭失效数据源时发生异常: {}", e.getMessage());
                }
            }
        }
        
        // 移除失效的缓存项
        for (String key : keysToRemove) {
            serviceCache.remove(key);
            datasourceCache.remove(key);
            log.debug("移除失效的数据源缓存: {}", key);
        }
        
        log.debug("数据源缓存清理完成，移除了 {} 个失效项", keysToRemove.size());
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
                // 端口号必填，不提供默认值
                if (portObj == null) {
                    throw new IllegalArgumentException("数据源配置缺少必填参数：port");
                }
                int port;
                if (portObj instanceof Integer) {
                    port = (Integer) portObj;
                } else if (portObj instanceof String) {
                    port = Integer.parseInt((String) portObj);
                } else {
                    throw new IllegalArgumentException("端口号类型错误，应为Integer或String: " + portObj.getClass());
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
     * 使用Anyline的DatabaseType来构建标准的JDBC URL
     * 
     * @param datasourceType 数据源类型（必须是Anyline DatabaseType支持的类型）
     * @param host 主机地址
     * @param port 端口号（必填，不接受0或负数）
     * @param database 数据库名
     * @return JDBC URL
     * @throws IllegalArgumentException 如果参数无效或数据库类型不支持
     */
    public String buildJdbcUrl(String datasourceType, String host, int port, String database) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("主机地址不能为空");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("端口号必须在1-65535之间，当前值: " + port);
        }
        
        // 验证数据库类型是否被Anyline支持
        DatabaseType dbType;
        try {
            dbType = DatabaseType.valueOf(datasourceType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的数据源类型: " + datasourceType);
        }
        
        // 使用Anyline的url()方法获取URL模板，然后替换占位符
        // Anyline模板格式: jdbc:postgresql://{host}:{port:5432}/{database}
        String urlTemplate = dbType.url();
        String databasePart = (database != null && !database.trim().isEmpty()) ? database : "";
        
        // 替换Anyline模板中的占位符
        String url = urlTemplate
                .replace("{host}", host)
                .replace("{database}", databasePart)
                .replace("{database/schema}", databasePart); // DM数据库使用的占位符
        
        // 处理端口占位符 {port:默认值}，使用正则表达式替换
        url = url.replaceAll("\\{port:\\d+\\}", String.valueOf(port));
        
        return url;
    }

    /**
     * 根据数据源类型获取驱动类名
     * 直接使用Anyline的DatabaseType枚举，不支持的类型将抛出异常
     *
     * @param datasourceType 数据源类型字符串
     * @return 驱动类名
     * @throws IllegalArgumentException 如果数据源类型不被Anyline支持
     */
    public String getDriverByType(String datasourceType) {
        DatabaseType dbType = DatabaseType.valueOf(datasourceType);
        return dbType.driver();
    }

    /**
     * 根据数据源类型获取连接测试查询语句
     * <p>
     * 不同数据库使用不同的测试查询语句：
     * - PostgreSQL/KingBase: SELECT 1
     * - DM(达梦): SELECT 1 FROM DUAL
     * - Oracle: SELECT 1 FROM DUAL
     * - MySQL: SELECT 1
     *
     * @param datasourceType 数据源类型字符串，如"PostgreSQL"、"DM"、"KingBase"
     * @return 连接测试查询SQL
     */
    private String getConnectionTestQuery(String datasourceType) {
        try {
            DatabaseType dbType = DatabaseType.valueOf(datasourceType);
            
            switch (dbType) {
                case DM:
                case ORACLE:
                    // 达梦和Oracle使用FROM DUAL
                    return "SELECT 1 FROM DUAL";
                case PostgreSQL:
                case KingBase:
                case MySQL:
                default:
                    // PostgreSQL、金仓、MySQL等使用简单的SELECT 1
                    return "SELECT 1";
            }
        } catch (IllegalArgumentException e) {
            // 如果数据库类型无法识别，使用最通用的查询
            log.warn("无法识别的数据库类型[{}]，使用默认测试查询SELECT 1", datasourceType);
            return "SELECT 1";
        }
    }

}
