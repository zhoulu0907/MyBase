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

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApplicationDataSourceManager {
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();
    private static final Map<String, DataSource> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, DataSource> CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
                String tp = type == null ? "" : type.toUpperCase();
                String defaultPort = null;
                String prefix = "jdbc:postgresql://";
                if ("POSTGRESQL".equals(tp)) { defaultPort = "5432"; prefix = "jdbc:postgresql://"; }
                else if ("MYSQL".equals(tp)) { defaultPort = "3306"; prefix = "jdbc:mysql://"; }
                else if ("CLICKHOUSE".equals(tp)) { defaultPort = "8123"; prefix = "jdbc:clickhouse://"; }
                else if ("KINGBASE".equals(tp)) { defaultPort = "54321"; prefix = "jdbc:kingbase8://"; }
                else if ("TDENGINE".equals(tp)) { defaultPort = "6030"; prefix = "jdbc:TAOS://"; }
                String p = (port == null || port.isBlank()) ? (defaultPort == null ? "" : defaultPort) : port;
                if (host != null && database != null) {
                    url = prefix + host + (p.isBlank() ? "" : (":" + p)) + "/" + database;
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
}
