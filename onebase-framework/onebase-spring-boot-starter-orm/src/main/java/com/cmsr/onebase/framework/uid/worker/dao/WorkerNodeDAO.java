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
 *
 * @author yutianbao
 */
public class WorkerNodeDAO {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private volatile String databaseType;

    public WorkerNodeDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    /**
     * 获取数据库类型（懒加载）
     *
     * @return 数据库类型（小写）
     */
    private String getDatabaseType() {
        if (databaseType == null) {
            synchronized (this) {
                if (databaseType == null) {
                    try {
                        String driverClassName = dataSource.getConnection().getMetaData().getDriverName().toLowerCase();
                        if (driverClassName.contains("dm") || driverClassName.contains("dameng")) {
                            databaseType = "dameng";
                        } else if (driverClassName.contains("mysql")) {
                            databaseType = "mysql";
                        } else if (driverClassName.contains("postgresql")) {
                            databaseType = "postgresql";
                        } else if (driverClassName.contains("oracle")) {
                            databaseType = "oracle";
                        } else if (driverClassName.contains("kingbase") || driverClassName.contains("人大金仓")) {
                            databaseType = "kingbase";
                        } else {
                            databaseType = "unknown";
                        }
                    } catch (Exception e) {
                        databaseType = "postgresql";
                    }
                }
            }
        }
        return databaseType;
    }

    /**
     * Add {@link WorkerNodeEntity}
     *
     * @param workerNodeEntity 工作节点实体
     */
    public void addWorkerNode(WorkerNodeEntity workerNodeEntity) {
        String dbType = getDatabaseType();
        // 达梦数据库：需要手动生成ID（表中id列不是自增的）
        if ("dameng".equals(dbType)) {
            // 先查询当前最大ID
            Long maxId = jdbcTemplate.queryForObject(
                    """
                            SELECT COALESCE(MAX("id"), 0) FROM "system_uid_worker_node""",
                    Long.class
            );
            Long newId = (maxId != null ? maxId : 0L) + 1;
            // 插入时显式指定ID和所有必需字段
            String sql = """
                    INSERT INTO "system_uid_worker_node"
                    ("id", "worker_host", "worker_port", "node_type", "launch_date", "creator",
                    "create_time", "updater", "update_time", "deleted")
                    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, 0)""";

            jdbcTemplate.update(sql, newId,
                    workerNodeEntity.getWorkerHost(),
                    workerNodeEntity.getWorkerPort(),
                    workerNodeEntity.getNodeType());

            workerNodeEntity.setId(newId);
            return;
        }

        // 其他数据库的处理逻辑
        String sql;
        if ("kingbase".equals(dbType)) {
            // 人大金仓：显式使用序列生成ID
            sql = """
                    INSERT INTO "system_uid_worker_node" ("id", "worker_host", "worker_port", "node_type")
                    VALUES (nextval('seq_system_uid_worker_node'), ?, ?, ?)""";
        } else if ("oracle".equals(dbType)) {
            // Oracle：显式使用序列生成ID
            sql = """
                    INSERT INTO "system_uid_worker_node" ("id", "worker_host", "worker_port", "node_type")
                    VALUES (seq_system_uid_worker_node.NEXTVAL, ?, ?, ?)""";
        } else {
            // MySQL、PostgreSQL等：依赖自增主键
            sql = """
                    INSERT INTO "system_uid_worker_node" ("worker_host", "worker_port", "node_type")
                    VALUES (?, ?, ?)""";
        }

        // 使用KeyHolder获取自动生成的ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, workerNodeEntity.getWorkerHost());
            ps.setString(2, workerNodeEntity.getWorkerPort());
            ps.setInt(3, workerNodeEntity.getNodeType());
            return ps;
        }, keyHolder);

        // 获取自增ID（兼容不同数据库的键名：id, ID, GENERATED_KEY等）
        Number generatedId = null;

        // 方式1：尝试从keyHolder直接获取
        try {
            if (keyHolder.getKey() != null) {
                generatedId = keyHolder.getKey();
            }
        } catch (Exception e) {
            // 如果失败，继续使用方式2
            generatedId = null;
        }

        // 方式2：从keyList中获取
        if (generatedId == null) {
            List<Map<String, Object>> keyList = keyHolder.getKeyList();
            if (keyList != null && !keyList.isEmpty()) {
                Map<String, Object> map = keyList.get(0);
                // 尝试不同的键名
                generatedId = (Number) map.get("id");
                if (generatedId == null) {
                    generatedId = (Number) map.get("ID");
                }
                if (generatedId == null) {
                    generatedId = (Number) map.get("GENERATED_KEY");
                }
                if (generatedId == null && !map.isEmpty()) {
                    generatedId = (Number) map.values().iterator().next();
                }
            }
        }
        if (generatedId == null) {
            throw new RuntimeException("Failed to get generated key for worker node, database type: " + databaseType);
        }
        workerNodeEntity.setId(generatedId.longValue());
    }

}
