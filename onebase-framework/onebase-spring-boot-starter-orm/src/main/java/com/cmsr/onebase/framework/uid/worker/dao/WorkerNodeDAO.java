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

    public WorkerNodeDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Add {@link WorkerNodeEntity}
     *
     * @param workerNodeEntity
     */
    public void addWorkerNode(WorkerNodeEntity workerNodeEntity) {
        // 定义SQL插入语句（加双引号以支持达梦等大小写敏感数据库）
        String sql = "insert into \"system_uid_worker_node\" (\"worker_host\", \"worker_port\", \"node_type\") values (?, ?, ?)";
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
        
        // 方式1：尝试从keyHolder直接获取（适用于只返回主键的数据库，如达梦）
        try {
            if (keyHolder.getKey() != null) {
                generatedId = keyHolder.getKey();
            }
        } catch (Exception e) {
            // 如果失败（如PostgreSQL返回多个列），继续使用方式2
            generatedId = null;
        }
        
        // 方式2：从keyList中获取（兼容返回所有列的数据库，如PostgreSQL）
        if (generatedId == null) {
            List<Map<String, Object>> keyList = keyHolder.getKeyList();
            if (keyList != null && !keyList.isEmpty()) {
                Map<String, Object> map = keyList.get(0);
                // 尝试不同的键名（小写id、大写ID、GENERATED_KEY等）
                generatedId = (Number) map.get("id");
                if (generatedId == null) {
                    generatedId = (Number) map.get("ID");
                }
                if (generatedId == null) {
                    generatedId = (Number) map.get("GENERATED_KEY");
                }
                if (generatedId == null && !map.isEmpty()) {
                    // 如果以上都没找到，取第一个值（通常就是ID）
                    generatedId = (Number) map.values().iterator().next();
                }
            }
        }
        
        if (generatedId == null) {
            throw new RuntimeException("Failed to get generated key for worker node");
        }
        workerNodeEntity.setId(generatedId.longValue());
    }

}
