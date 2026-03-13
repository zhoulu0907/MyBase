package com.cmsr.onebase.module.metadata.core.dal.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * OpenGauss 兼容 JDBC Driver。
 *
 * <p>对外接受 jdbc:opengauss:// 前缀，内部转换为 jdbc:postgresql:// 并委托给实际驱动处理。</p>
 *
 * @author bty418
 * @date 2026-03-13
 */
public class OpenGaussCompatibleDriver implements Driver {

    private static final String OPENGAUSS_PREFIX = "jdbc:opengauss://";
    private static final String POSTGRES_PREFIX = "jdbc:postgresql://";

    static {
        try {
            DriverManager.registerDriver(new OpenGaussCompatibleDriver());
        } catch (SQLException e) {
            throw new RuntimeException("注册 OpenGaussCompatibleDriver 失败", e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        Driver delegate = findDelegate();
        String convertedUrl = convertUrl(url);
        return delegate.connect(convertedUrl, info);
    }

    @Override
    public boolean acceptsURL(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.startsWith(OPENGAUSS_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        Driver delegate = findDelegate();
        return delegate.getPropertyInfo(convertUrl(url), info);
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(OpenGaussCompatibleDriver.class.getName());
    }

    private Driver findDelegate() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("未找到 org.postgresql.Driver，请检查 opengauss-jdbc 依赖", e);
        }
        Driver delegate = DriverManager.getDriver(POSTGRES_PREFIX);
        if (delegate == null) {
            throw new SQLException("未找到可用的 PostgreSQL/OpenGauss JDBC 驱动");
        }
        return delegate;
    }

    private String convertUrl(String url) {
        if (url == null) {
            return null;
        }
        if (url.startsWith(OPENGAUSS_PREFIX)) {
            return POSTGRES_PREFIX + url.substring(OPENGAUSS_PREFIX.length());
        }
        return url;
    }
}