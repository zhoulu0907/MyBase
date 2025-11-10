package com.cmsr.onebase.module.etl.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DatasourceFactory {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{([^{}:]+)(:[^{}]+)?\\}");

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    public DataSource constructDataSource(ETLDatasourceDO datasourceDO, boolean oneshot) {
        // 1. 获取数据库类型
        String databaseType = datasourceDO.getDatasourceType();
        DatabaseType dbType = parseDatabaseType(databaseType);
        // 2. 创建DataSource
        Properties connectionProperties = JsonUtils.parseObject(datasourceDO.getConfig(), Properties.class);
        String connectMode = (String) connectionProperties.getOrDefault("connectMode", "default");
        String jdbcConnection;
        if (StringUtils.equalsIgnoreCase("default", connectMode)) {
            jdbcConnection = buildJdbcConnectionString(dbType, connectionProperties);
        } else {
            jdbcConnection = (String) connectionProperties.get("jdbcUrl");
        }

        String username = (String) connectionProperties.get("username");
        String password = (String) connectionProperties.get("password");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_PROPERTY_INSUFFICIENT);
        }
        if (oneshot) {
            return new DriverManagerDataSource(
                    jdbcConnection,
                    username,
                    password
            );
        } else {
            return new SingleConnectionDataSource(
                    jdbcConnection,
                    username,
                    password,
                    false
            );
        }
    }

    public DataSource constructDataSource(Long datasourceId, boolean oneshot) {
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        return constructDataSource(datasourceDO, oneshot);
    }

    private DatabaseType parseDatabaseType(String databaseType) {
        if (StringUtils.isBlank(databaseType)) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_ILLEGAL);
        }
        DatabaseType parseType = null;
        for (DatabaseType dbType : DatabaseType.values()) {
            if (dbType.title().equalsIgnoreCase(databaseType)) {
                parseType = dbType;
                break;
            }
        }
        if (parseType == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_DATASOURCE_TYPE);
        }
        String driverClass = parseType.driver();
        if (StringUtils.isBlank(driverClass)) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_SUPPORTED);
        }
        return parseType;
    }

    private Driver getDeclaredDriverInstance(DatabaseType dbType) {
        String driverName = dbType.driver();
        try {
            Class<? extends Driver> driverClass = (Class<? extends Driver>) ClassUtils.getClass(driverName);
            return driverClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException ex) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_SUPPORTED);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            log.error("JDBC连接驱动初始化失败", ex);
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.UNKNOWN_ERROR);
        }
    }

    /**
     * 构建JDBC连接字符串
     * 使用Anyline的DatabaseType提供的URL模板，替换占位符
     *
     * @param dbType               数据库类型
     * @param connectionProperties 连接属性
     * @return JDBC连接字符串
     */
    private String buildJdbcConnectionString(DatabaseType dbType, Properties connectionProperties) {
        // 直接使用Anyline提供的URL模板
        String jdbcTemplate = dbType.url();
        // 魔法处理，Anyline的PostgreSQL类数据源URL定义有错误
        if (StringUtils.equals("org.postgresql.Driver", dbType.driver())) {
            jdbcTemplate = "jdbc:postgresql://{host}:{port:5432}/{database}";
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(jdbcTemplate);
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            Object property = connectionProperties.get(propertyName);
            if (ObjectUtils.isEmpty(property)) {
                throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_PROPERTY_INSUFFICIENT);
            }
            String propertyStr = String.valueOf(property);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propertyStr));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
