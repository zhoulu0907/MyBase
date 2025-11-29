package com.cmsr.onebase.module.metadata.runtime.semantic;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class DynamicMetadataRepository {

    @Resource
    private SemanticTableNameQuoter tableNameQuoter;

    public int insert(String tableName, Row row) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (row.containsKey("created_time")) { row.put("created_time", now); }
        if (row.containsKey("createtime")) { row.put("createtime", now); }
        if (row.containsKey("updated_time")) { row.put("updated_time", now); }
        if (row.containsKey("updatetime")) { row.put("updatetime", now); }
        if (row.containsKey("deleted")) { row.put("deleted", 0); }
        if (row.containsKey("lock_version")) { row.put("lock_version", 0); }
        if (row.containsKey("lockversion")) { row.put("lockversion", 0); }
        if (userId != null) {
            if (row.containsKey("creator")) { row.put("creator", userId); }
            if (row.containsKey("updater")) { row.put("updater", userId); }
        }
        return Db.insert(tableNameQuoter.quote(tableName), row);
    }

    public int updateByQuery(String tableName, Row row, QueryWrapper qw) {
        return Db.updateByQuery(tableNameQuoter.quote(tableName), row, qw);
    }

    public int softDeleteByQuery(String tableName, QueryWrapper qw) {
        Row row = new Row();
        row.put("deleted", 1);
        return Db.updateByQuery(tableNameQuoter.quote(tableName), row, qw);
    }

    public int deleteByQuery(String tableName, QueryWrapper qw) {
        return Db.deleteByQuery(tableNameQuoter.quote(tableName), qw);
    }

    public Row selectOneByQuery(String tableName, QueryWrapper qw) {
        return Db.selectOneByQuery(tableNameQuoter.quote(tableName), qw);
    }

    public List<Row> selectListByQuery(String tableName, QueryWrapper qw) {
        return Db.selectListByQuery(tableNameQuoter.quote(tableName), qw);
    }
}
