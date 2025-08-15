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
        // 定义SQL插入语句
        String sql = "insert into system_uid_worker_node (worker_host, worker_port, node_type) values (?, ?, ?)";
        // 使用KeyHolder获取自动生成的ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, workerNodeEntity.getWorkerHost());
            ps.setString(2, workerNodeEntity.getWorkerPort());
            ps.setInt(3, workerNodeEntity.getNodeType());
            return ps;
        }, keyHolder);
        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        if (keyList == null || keyList.isEmpty()) {
            throw new RuntimeException("Failed to get generated key for worker node");
        }
        Map<String, Object> map = keyList.get(0);
        Number id = (Number) map.get("id");
        if (id == null) {
            throw new RuntimeException("Failed to get generated key for worker node");
        }
        workerNodeEntity.setId(id.longValue());
    }

}
