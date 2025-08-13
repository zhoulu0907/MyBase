package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 临时数据源服务类
 * <p>
 * 专门负责创建临时的AnylineService实例，用于连接到不同的数据源进行操作
 *
 * @author matianyu
 * @date 2025-08-06
 */
@Component
@Slf4j
public class TemporaryDatasourceService {

    @Resource
    private DatasourceConvert datasourceConvert;
    
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
        Map<String, Object> config = datasourceConvert.stringToMap(datasource.getConfig());
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

            // 创建数据源配置 - 使用Druid连接池
            Map<String, Object> dsConfig = new HashMap<>();
            dsConfig.put("url", url);
            dsConfig.put("username", username != null ? username : "");
            dsConfig.put("password", password != null ? password : "");
            dsConfig.put("driver", getDriverByType(datasourceType));
            // 指定连接池类型，使用Druid
            dsConfig.put("pool", "com.alibaba.druid.pool.DruidDataSource");
            // 添加Druid连接池配置
            dsConfig.put("initial-size", 1);
            dsConfig.put("max-active", 10);
            dsConfig.put("min-idle", 1);
            dsConfig.put("max-wait", 30000);
            dsConfig.put("validation-query", "SELECT 1");
            dsConfig.put("test-on-borrow", false);
            dsConfig.put("test-on-return", false);
            dsConfig.put("test-while-idle", true);
            dsConfig.put("time-between-eviction-runs-millis", 60000);
            
            log.info("临时数据源配置: {}", dsConfig);
            
            // 创建数据源和临时服务
            DataSource dataSource = DataSourceUtil.build(dsConfig);
            AnylineService<?> service = ServiceProxy.temporary(dataSource);
            
            // 缓存创建的服务
            serviceCache.put(cacheKey, service);
            
            log.info("临时服务创建成功，Service实例: {}", service.getClass().getName());
            log.info("=== 创建临时数据源调试信息结束 ===");
            
            return service;
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
