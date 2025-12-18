/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cmsr.onebase.framework.uid.worker.dao;

import com.cmsr.onebase.framework.uid.worker.entity.WorkerNodeEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * DAO for M_WORKER_NODE
 * 支持多种数据库类型的分布式ID工作节点管理
 *
 * @author yutianbao
 */
public class WorkerNodeDAO {

    // 数据库类型常量
    private static final String MYSQL = "mysql";
    private static final String POSTGRESQL = "postgresql";
    private static final String DM = "dm";
    private static final String KINGBASE = "kingbase";
    private static final String ORACLE = "oracle";
    private static final String SQLSERVER = "sqlserver";
    private static final String OTHER = "other";

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private volatile String databaseType;

    public WorkerNodeDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    /**
     * 获取数据库类型（懒加载）
     * 支持多种数据库类型检测，包括国产数据库
     *
     * @return 数据库类型字符串
     */
    private String getDatabaseType() {
        if (databaseType == null) {
            synchronized (this) {
                if (databaseType == null) {
                    try {
                        String driverName = dataSource.getConnection().getMetaData().getDriverName().toLowerCase();

                        // 使用if-else判断数据库类型
                        if (driverName.contains("mysql")) {
                            databaseType = MYSQL;
                        } else if (driverName.contains("postgresql")) {
                            databaseType = POSTGRESQL;
                        } else if (driverName.contains("dm")) {
                            databaseType = DM;
                        } else if (driverName.contains("kingbase")) {
                            databaseType = KINGBASE;
                        } else if (driverName.contains("oracle")) {
                            databaseType = ORACLE;
                        } else if (driverName.contains("sqlserver") || driverName.contains("microsoft")) {
                            databaseType = SQLSERVER;
                        } else {
                            databaseType = OTHER;
                        }

                    } catch (Exception e) {
                        // 检测失败时使用默认类型
                        databaseType = OTHER;
                    }
                }
            }
        }
        return databaseType;
    }

    /**
     * 添加工作节点实体
     * 根据当前数据库类型使用if-else条件逻辑进行插入操作
     *
     * @param workerNodeEntity 工作节点实体
     */
    public void addWorkerNode(WorkerNodeEntity workerNodeEntity) {
        String dbType = getDatabaseType();
        try {
            // 使用if-else条件逻辑处理不同数据库类型
            if (MYSQL.equals(dbType) || POSTGRESQL.equals(dbType) || SQLSERVER.equals(dbType)) {
                // 支持自增ID的数据库
                insertWithAutoIncrement(workerNodeEntity);
            } else {
                // 其他数据库（包括国产数据库）使用MAX+1策略
                insertWithMaxId(workerNodeEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add worker node, database type: " + dbType, e);
        }
    }

    /**
     * 使用自增ID方式插入工作节点
     * 适用于MySQL、PostgreSQL、SQLServer等支持自增的数据库
     *
     * @param workerNodeEntity 工作节点实体
     */
    private void insertWithAutoIncrement(WorkerNodeEntity workerNodeEntity) {
        String sql = """
                INSERT INTO system_uid_worker_node(worker_host, worker_port, node_type)
                VALUES(?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, workerNodeEntity.getWorkerHost());
            ps.setString(2, workerNodeEntity.getWorkerPort());
            ps.setInt(3, workerNodeEntity.getNodeType());
            return ps;
        }, keyHolder);

        // 获取自增ID
        Number generatedId = extractGeneratedId(keyHolder);
        if (generatedId == null) {
            throw new RuntimeException("Failed to get generated key for worker node");
        }
        workerNodeEntity.setId(generatedId.longValue());
    }

    /**
     * 从KeyHolder中提取生成的ID
     * 提供多种提取方式以增强兼容性
     *
     * @param keyHolder KeyHolder对象
     * @return 生成的ID，如果无法提取则返回null
     */
    private Number extractGeneratedId(KeyHolder keyHolder) {
        // 方式1：尝试从keyHolder直接获取
        try {
            if (keyHolder.getKey() != null) {
                return keyHolder.getKey();
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他方式
        }
        // 方式2：从keyList中获取
        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        if (keyList != null && !keyList.isEmpty()) {
            Map<String, Object> map = keyList.get(0);
            // 尝试不同的键名
            Number generatedId = (Number) map.get("id");
            if (generatedId == null) {
                generatedId = (Number) map.get("ID");
            }
            if (generatedId == null) {
                generatedId = (Number) map.get("GENERATED_KEY");
            }
            if (generatedId == null && !map.isEmpty()) {
                generatedId = (Number) map.values().iterator().next();
            }
            return generatedId;
        }
        return null;
    }

    /**
     * 使用MAX+1策略插入工作节点
     * 适用于不支持自增的数据库（包括国产数据库）
     *
     * @param workerNodeEntity 工作节点实体
     */
    private void insertWithMaxId(WorkerNodeEntity workerNodeEntity) {
        String dbType = getDatabaseType();
        String insertSql;
        String maxIdSql;

        // 使用if-else判断数据库类型，使用JDK 17三引号语法
        if (KINGBASE.equals(dbType) || DM.equals(dbType)) {
            // KingBase、达梦数据库需要双引号
            insertSql = """
                    INSERT INTO "system_uid_worker_node" ("id", "worker_host", "worker_port", "node_type")
                    VALUES(?, ?, ?, ?)
                    """;
            maxIdSql = """
                    SELECT MAX("id") FROM "system_uid_worker_node"
                    """;
        } else {
            // 其他不支持自增的数据库不需要双引号
            insertSql = """
                    INSERT INTO system_uid_worker_node(id, worker_host, worker_port, node_type)
                    VALUES(?, ?, ?, ?)
                    """;
            maxIdSql = """
                    SELECT MAX(id) FROM system_uid_worker_node
                    """;
        }

        // 重试机制，避免并发冲突
        for (int i = 0; i < 10; i++) {
            try {
                // 查询当前最大ID
                Long maxId = jdbcTemplate.queryForObject(maxIdSql, Long.class);
                long newId = (maxId != null ? maxId : 0L) + 1;
                // 插入新记录
                jdbcTemplate.update(insertSql, newId,
                        workerNodeEntity.getWorkerHost(),
                        workerNodeEntity.getWorkerPort(),
                        workerNodeEntity.getNodeType());

                workerNodeEntity.setId(newId);
                return;
            } catch (DataIntegrityViolationException e) {
                // 主键冲突，重试
                if (i == 9) {
                    throw new RuntimeException("Failed to add worker node after 10 retries due to duplicate key", e);
                }
            }
        }
    }

}