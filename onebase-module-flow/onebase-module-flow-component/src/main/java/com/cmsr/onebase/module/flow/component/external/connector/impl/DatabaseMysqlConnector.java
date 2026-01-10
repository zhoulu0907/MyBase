package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.cmsr.onebase.module.flow.component.external.connector.AbstractConnector;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorExecutor;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL数据库连接器实现
 * 支持数据库查询和更新操作
 *
 * @author zhoulu
 * @since 2025-01-10
 */
@Slf4j
@Component
public class DatabaseMysqlConnector extends AbstractConnector {

    @Override
    public String getConnectorType() {
        return "DATABASE_MYSQL";
    }

    @Override
    public String getConnectorName() {
        return "MySQL数据库连接器";
    }

    @Override
    public String getConnectorDescription() {
        return "MySQL数据库连接器，支持查询和更新操作";
    }

    @Override
    protected Map<String, Object> doExecute(String actionType, Map<String, Object> config) throws Exception {
        // 获取数据库连接
        try (Connection connection = createDatabaseConnection(config)) {
            // 执行数据库操作
            String operationType = config.getOrDefault("operationType", "QUERY").toString();

            switch (operationType) {
                case "QUERY":
                    return executeQuery(connection, config, actionType);
                case "UPDATE":
                    return executeUpdate(connection, config, actionType);
                case "INSERT":
                    return executeInsert(connection, config, actionType);
                case "DELETE":
                    return executeDelete(connection, config, actionType);
                default:
                    throw new ConnectorExecutionException(getConnectorType(), actionType, "不支持的数据库操作类型: " + operationType);
            }
        } catch (ConnectorExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "数据库操作失败", e);
        }
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        return config != null &&
               config.containsKey("jdbcUrl") &&
               config.containsKey("username") &&
               config.containsKey("password");
    }

    /**
     * 创建数据库连接
     */
    private Connection createDatabaseConnection(Map<String, Object> config) throws SQLException {
        String jdbcUrl = config.get("jdbcUrl").toString();
        String username = config.get("username").toString();
        String password = config.get("password").toString();

        log.info("创建MySQL数据库连接，URL: {}", jdbcUrl);

        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * 执行查询操作
     */
    private Map<String, Object> executeQuery(Connection connection, Map<String, Object> config, String actionType) throws Exception {
        Map<String, Object> inputData = getInputData(config);

        String sql = getStringValue(inputData, "sql", "");
        if (sql.trim().isEmpty()) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "SQL语句不能为空");
        }

        log.info("执行MySQL查询: {}", sql);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 设置参数
            setStatementParameters(stmt, inputData);

            // 执行查询
            try (ResultSet rs = stmt.executeQuery()) {
                // 处理结果集
                List<Map<String, Object>> resultList = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    resultList.add(row);
                }

                log.info("MySQL查询执行成功，返回 {} 行数据", resultList.size());

                return buildSuccessResult("查询成功", Map.of(
                        "data", resultList,
                        "rowCount", resultList.size()
                ));
            }
        }
    }

    /**
     * 执行更新操作
     */
    private Map<String, Object> executeUpdate(Connection connection, Map<String, Object> config, String actionType) throws Exception {
        Map<String, Object> inputData = getInputData(config);

        String sql = getStringValue(inputData, "sql", "");
        if (sql.trim().isEmpty()) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "SQL语句不能为空");
        }

        log.info("执行MySQL更新: {}", sql);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 设置参数
            setStatementParameters(stmt, inputData);

            // 执行更新
            int affectedRows = stmt.executeUpdate();

            log.info("MySQL更新执行成功，影响 {} 行", affectedRows);

            return buildSuccessResult("更新成功", Map.of("affectedRows", affectedRows));
        }
    }

    /**
     * 执行插入操作
     */
    private Map<String, Object> executeInsert(Connection connection, Map<String, Object> config, String actionType) throws Exception {
        Map<String, Object> inputData = getInputData(config);

        String sql = getStringValue(inputData, "sql", "");
        if (sql.trim().isEmpty()) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "SQL语句不能为空");
        }

        log.info("执行MySQL插入: {}", sql);

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 设置参数
            setStatementParameters(stmt, inputData);

            // 执行插入
            int affectedRows = stmt.executeUpdate();

            // 获取生成的键值
            Object generatedKey = null;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1);
                }
            }

            log.info("MySQL插入执行成功，影响 {} 行，生成键: {}", affectedRows, generatedKey);

            Map<String, Object> data = new HashMap<>();
            data.put("affectedRows", affectedRows);
            data.put("generatedKey", generatedKey);

            return buildSuccessResult("插入成功", data);
        }
    }

    /**
     * 执行删除操作
     */
    private Map<String, Object> executeDelete(Connection connection, Map<String, Object> config, String actionType) throws Exception {
        Map<String, Object> inputData = getInputData(config);

        String sql = getStringValue(inputData, "sql", "");
        if (sql.trim().isEmpty()) {
            throw new ConnectorExecutionException(getConnectorType(), actionType, "SQL语句不能为空");
        }

        log.info("执行MySQL删除: {}", sql);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 设置参数
            setStatementParameters(stmt, inputData);

            // 执行删除
            int affectedRows = stmt.executeUpdate();

            log.info("MySQL删除执行成功，影响 {} 行", affectedRows);

            return buildSuccessResult("删除成功", Map.of("affectedRows", affectedRows));
        }
    }

    /**
     * 设置PreparedStatement参数
     */
    private void setStatementParameters(PreparedStatement stmt, Map<String, Object> inputData) throws SQLException {
        if (inputData == null) {
            return;
        }

        Object parameters = inputData.get("parameters");
        if (parameters instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> paramList = (List<Object>) parameters;
            for (int i = 0; i < paramList.size(); i++) {
                stmt.setObject(i + 1, paramList.get(i));
            }
        } else if (parameters instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> paramMap = (Map<String, Object>) parameters;
            int paramIndex = 1;
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                stmt.setObject(paramIndex++, entry.getValue());
            }
        }
    }
}
