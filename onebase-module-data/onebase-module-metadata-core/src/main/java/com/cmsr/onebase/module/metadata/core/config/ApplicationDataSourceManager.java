package com.cmsr.onebase.module.metadata.core.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.datasource.DataSourceKey;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.anyline.metadata.type.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ApplicationDataSourceManager {
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();
    private static final Map<String, DataSource> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, DataSource> CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{([^{}:]+)(:[^{}]+)?\\}");

    public static String useBizDatasourceByAppId(Long appId) {
        if (appId == null) { return null; }
        String key = buildKey(appId);

        DataSource cached = CACHE.get(key);
        if (isActive(cached)) {
            registerIfNeeded(key, cached);
            DataSourceKey.use(key);
            CURRENT_KEY.set(key);
            return key;
        }

        List<MetadataDatasourceDO> list = getDatasourcesByApplicationId(appId);
        if (list == null || list.isEmpty()) { throw new RuntimeException("未找到应用绑定的数据源"); }
        MetadataDatasourceDO selected = selectPreferredDatasource(list);
        String cfgKey = computeConfigKey(selected);
        DataSource ds = CONFIG_CACHE.get(cfgKey);
        if (!isActive(ds)) {
            ds = buildDataSourceFromConfig(selected);
            CONFIG_CACHE.put(cfgKey, ds);
            try {
                FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
                if (config != null && config.getDataSource() != null) {
                    config.getDataSource().addDataSource(key, ds);
                }
            } catch (Exception ignore) {
                throw new RuntimeException("添加数据源到 FlexGlobalConfig 失败", ignore);
            }
        }
        registerIfNeeded(key, ds);
        CACHE.put(key, ds);
        DataSourceKey.use(key);
        CURRENT_KEY.set(key);
        return key;
    }

    public static void clear() {
        DataSourceKey.clear();
        CURRENT_KEY.remove();
    }

    private static boolean isActive(DataSource dataSource) {
        if (dataSource == null) return false;
        try (Connection conn = dataSource.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    private static void registerIfNeeded(String key, DataSource dataSource) {
        try {
            FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
            if (config == null) return;
            FlexDataSource flexDataSource = config.getDataSource();
            if (flexDataSource == null) return;
            try { flexDataSource.addDataSource(key, dataSource); } catch (Exception ignore) {}
        } catch (Exception ignore) {
        }
    }

    private static List<MetadataDatasourceDO> getDatasourcesByApplicationId(Long appId) {
        MetadataAppAndDatasourceCoreService svc = SpringUtil.getBean(MetadataAppAndDatasourceCoreService.class);
        return svc.getDatasourcesByApplicationId(appId);
    }

    private static MetadataDatasourceDO selectPreferredDatasource(List<MetadataDatasourceDO> list) {
        for (MetadataDatasourceDO ds : list) {
            Integer origin = ds.getDatasourceOrigin();
            if (origin != null && Integer.valueOf(0).equals(origin)) { return ds; }
        }
        return list.get(0);
    }

    private static DataSource buildDataSourceFromConfig(Map<String, Object> cfg) {
        try {
            String url = normalizeUrl(cfg);
            String username = str(cfg.get("username"));
            if (username == null) { username = str(cfg.get("user")); }
            String password = str(cfg.get("password"));

            HikariDataSource hikari = new HikariDataSource();
            if (url != null) { hikari.setJdbcUrl(url); }
            if (username != null) { hikari.setUsername(username); }
            if (password != null) { hikari.setPassword(password); }
            hikari.setMaximumPoolSize(5);
            hikari.setMinimumIdle(1);
            hikari.setConnectionTimeout(30000);
            hikari.setIdleTimeout(300000);
            hikari.setMaxLifetime(1800000);
            return hikari;
        } catch (Exception e) {
            throw new RuntimeException("构建数据源失败: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> parseDsConfig(MetadataDatasourceDO dsDo) {
        String configJson = dsDo.getConfig();
        return parseConfig(configJson);
    }

    private static String computeConfigKey(MetadataDatasourceDO dsDo) {
        if (dsDo == null) { return sha256("null"); }
        String configJson = dsDo.getConfig();
        String raw = (configJson == null ? "" : configJson)
                + "|" + (dsDo.getDatasourceType() == null ? "" : dsDo.getDatasourceType())
                + "|" + (dsDo.getCode() == null ? "" : dsDo.getCode());
        return sha256(raw);
    }

    private static DataSource buildDataSourceFromConfig(MetadataDatasourceDO dsDo) {
        try {
            log.info("构建数据源配置: {}", dsDo.getConfig());
            Map<String, Object> cfg = parseConfig(dsDo == null ? null : dsDo.getConfig());
            String url = str(cfg.get("url"));
            String jdbcUrl = str(cfg.get("jdbcUrl"));
            if ((url == null || url.isBlank()) && jdbcUrl != null && !jdbcUrl.isBlank()) { url = jdbcUrl; }
            if (url == null || url.isBlank()) {
                String host = str(cfg.get("host"));
                String port = str(cfg.get("port"));
                String database = str(cfg.get("database"));
                String type = dsDo == null ? null : dsDo.getDatasourceType();
                if (host != null && database != null) {
                    url = buildJdbcUrl(type, host, port, database);
                }
            }
            String username = str(cfg.get("username"));
            if (username == null) { username = str(cfg.get("user")); }
            String password = str(cfg.get("password"));

            HikariDataSource hikari = new HikariDataSource();
            if (url != null) { hikari.setJdbcUrl(url); }
            if (username != null) { hikari.setUsername(username); }
            if (password != null) { hikari.setPassword(password); }
            hikari.setMaximumPoolSize(5);
            hikari.setMinimumIdle(1);
            hikari.setConnectionTimeout(30000);
            hikari.setIdleTimeout(300000);
            hikari.setMaxLifetime(1800000);
            return hikari;
        } catch (Exception e) {
            throw new RuntimeException("构建数据源失败: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> parseConfig(String json) {
        if (json == null || json.isBlank()) { return new HashMap<>(); }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private static String normalizeUrl(Map<String, Object> cfg) {
        String url = str(cfg.get("url"));
        String jdbcUrl = str(cfg.get("jdbcUrl"));
        if ((url == null || url.isBlank()) && jdbcUrl != null && !jdbcUrl.isBlank()) { url = jdbcUrl; }
        if (url == null || url.isBlank()) {
            String host = str(cfg.get("host"));
            String port = str(cfg.get("port"));
            String database = str(cfg.get("database"));
            if (host != null && database != null) {
                String p = (port == null || port.isBlank()) ? "5432" : port;
                url = "jdbc:postgresql://" + host + ":" + p + "/" + database;
            }
        }
        return url;
    }

    private static String computeConfigKey(Map<String, Object> cfg) {
        String url = normalizeUrl(cfg);
        String username = str(cfg.get("username"));
        if (username == null) { username = str(cfg.get("user")); }
        String password = str(cfg.get("password"));
        String raw = (url == null ? "" : url) + "|" + (username == null ? "" : username) + "|" + (password == null ? "" : password);
        return sha256(raw);
    }

    private static String sha256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) { sb.append(String.format("%02x", b)); }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }

    private static String str(Object obj) { return obj == null ? null : String.valueOf(obj); }

    private static String buildKey(Long appId) { return "app_ds_" + appId; }

    /**
     * 使用 Anyline DatabaseType 构建 JDBC URL
     * 复用框架内置的数据库类型配置，避免硬编码
     */
    private static String buildJdbcUrl(String datasourceType, String host, String port, String database) {
        if (host == null || host.isBlank() || database == null || database.isBlank()) {
            return null;
        }
        // 解析数据库类型，默认使用 PostgreSQL
        DatabaseType dbType = DatabaseType.POSTGRESQL;
        if (datasourceType != null && !datasourceType.isBlank()) {
            try {
                dbType = DatabaseType.valueOf(datasourceType.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("未知的数据源类型: {}，使用默认 PostgreSQL", datasourceType);
            }
        }
        // 获取 Anyline 内置的 URL 模板
        String jdbcTemplate = dbType.url();
        // Anyline 的 PostgreSQL 类数据源 URL 定义有问题，需要特殊处理
        if ("org.postgresql.Driver".equals(dbType.driver())) {
            jdbcTemplate = "jdbc:postgresql://{host}:{port:5432}/{database}";
        }
        // 替换模板中的占位符
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(jdbcTemplate);
        Map<String, String> properties = new HashMap<>();
        properties.put("host", host);
        properties.put("port", port != null && !port.isBlank() ? port : extractDefaultPort(jdbcTemplate));
        properties.put("database", database);
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            String defaultValue = matcher.group(2);
            String value = properties.get(propertyName);
            if ((value == null || value.isBlank()) && defaultValue != null) {
                value = defaultValue.substring(1); // 去掉冒号前缀
            }
            if (value != null && !value.isBlank()) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 从 URL 模板中提取默认端口号
     */
    private static String extractDefaultPort(String template) {
        Matcher m = Pattern.compile("\\{port:(\\d+)\\}").matcher(template);
        return m.find() ? m.group(1) : "5432";
    }
}
