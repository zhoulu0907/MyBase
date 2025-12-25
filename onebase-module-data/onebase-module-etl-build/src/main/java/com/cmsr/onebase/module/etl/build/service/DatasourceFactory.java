package com.cmsr.onebase.module.etl.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.enums.EtlErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.vo.ConnectCryptoProperties;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DatasourceFactory {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{([^{}:]+)(:[^{}]+)?\\}");

    public DataSource constructDataSource(String datasourceType, ConnectCryptoProperties connectProperties, boolean oneshot) {
        // 1. 获取数据库类型
        String connectMode = connectProperties.getConnectMode();
        String jdbcConnection = connectProperties.getJdbcUrl();
        if (StringUtils.isBlank(jdbcConnection)) {
            if (Strings.CI.equals(connectMode, "default")) {
                jdbcConnection = buildJdbcConnectionString(datasourceType, connectProperties);
            } else {
                throw new IllegalStateException();
            }
        }
        // 2. 创建DataSource
        String username = connectProperties.getUsername();
        String password = connectProperties.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_PROPERTY_INSUFFICIENT);
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

    public static DatabaseType parseDatabaseType(String databaseType) {
        if (StringUtils.isBlank(databaseType)) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_ILLEGAL);
        }
        DatabaseType parseType = null;
        for (DatabaseType dbType : DatabaseType.values()) {
            if (dbType.name().equalsIgnoreCase(databaseType)) {
                parseType = dbType;
                break;
            }
        }
        if (parseType == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.ILLEGAL_DATASOURCE_TYPE);
        }
        String driverClass = parseType.driver();
        if (StringUtils.isBlank(driverClass)) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_SUPPORTED);
        }
        return parseType;
    }

    public static String buildJdbcConnectionString(String databaseType, ConnectCryptoProperties connectProperties) {
        DatabaseType dbType = parseDatabaseType(databaseType);
        // 直接使用Anyline提供的URL模板
        String jdbcTemplate = dbType.url();
        // 魔法处理，Anyline的PostgreSQL类数据源URL定义有错误
        if (Strings.CS.equals("org.postgresql.Driver", dbType.driver())) {
            jdbcTemplate = "jdbc:postgresql://{host}:{port:5432}/{database}";
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(jdbcTemplate);
        Map<String, String> properties = new HashMap<>();
        properties.put("driver", dbType.driver());
        properties.put("host", connectProperties.getHost());
        properties.put("port", connectProperties.getPort());
        properties.put("database", connectProperties.getDatabase());
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            Object property = properties.get(propertyName);
            if (ObjectUtils.isEmpty(property)) {
                throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_PROPERTY_INSUFFICIENT);
            }
            String propertyStr = String.valueOf(property);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propertyStr));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
