package com.cmsr.onebase.module.metadata.runtime.semantic.dal.database;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class DynamicMetadataRepository {

    @Resource
    private SemanticTableNameQuoter tableNameQuoter;

    @Resource
    private UidGenerator uidGenerator;

    public int insert(String tableName, Row row) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long userDeptId = SecurityFrameworkUtils.getLoginUserDeptId() != null ? SecurityFrameworkUtils.getLoginUserDeptId() : 0L;

        if (row.containsKey("created_time")) { row.set("created_time", now); }
        if (row.containsKey("createtime")) { row.set("createtime", now); }
        if (row.containsKey("updated_time")) { row.set("updated_time", now); }
        if (row.containsKey("updatetime")) { row.set("updatetime", now); }
        if (row.containsKey("deleted")) { row.set("deleted", 0); }
        if (row.containsKey("lock_version")) { row.set("lock_version", 0); }
        if (row.containsKey("lockversion")) { row.set("lockversion", 0); }

        if (userId != null) {
            if (row.containsKey("owner_id") && row.get("owner_id") == null) { row.set("owner_id", userId); }
            if (row.containsKey("ownerid") && row.get("ownerid") == null) { row.set("ownerid", userId); }
            if (row.containsKey("creator") && row.get("creator") == null) { row.set("creator", userId); }
            if (row.containsKey("updater") && row.get("updater") == null) { row.set("updater", userId); }   
        }

        if (userDeptId != null) {
            if (row.containsKey("owner_dept") && row.get("owner_dept") == null) { row.set("owner_dept", userDeptId); }
            if (row.containsKey("ownerdept") && row.get("ownerdept") == null) { row.set("ownerdept", userDeptId); }
        }

        return Db.insert(tableNameQuoter.quote(tableName), row);
    }

    public int insertBatch(String tableName, List<Row> rows) {
        if (rows == null || rows.isEmpty()) { return 0; }
        int count = 0;
        for (Row r : rows) { count += insert(tableName, r); }
        return count;
    }

    public int updateByQuery(String tableName, Row row, QueryWrapper qw) {
        return Db.updateByQuery(tableNameQuoter.quote(tableName), row, qw);
    }

    public int softDeleteByQuery(String tableName, QueryWrapper qw) {
        Row update = new Row();
        update.put("deleted", 1);
        return Db.updateByQuery(tableNameQuoter.quote(tableName), update, qw);
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

    public Row selectMainById(String tableName, String pkField, Object id, boolean filterDeleted) {
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(String.valueOf(id)));
        if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
        return selectOneByQuery(tableName, qw);
    }

    public Row selectMainById(String tableName, String pkField, Object id) {
        return selectMainById(tableName, pkField, id, true);
    }

    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids, boolean filterDeleted) {
        if (ids == null || ids.isEmpty()) { return List.of(); }
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).in(ids));
        if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
        return selectListByQuery(tableName, qw);
    }

    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids) {
        return selectMainByIds(tableName, pkField, ids, true);
    }

    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId) {
        return selectSubtableRowsByParent(tableName, parentId, true);
    }

    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId, boolean filterDeleted) {
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn("parent_id").eq(String.valueOf(parentId)));
        if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
        return selectListByQuery(tableName, qw);
    }

    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue) {
        return selectRelationRowsByParent(tableName, relationKey, relationValue, true);
    }

    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue, boolean filterDeleted) {
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(relationKey).eq(String.valueOf(relationValue)));
        if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
        return selectListByQuery(tableName, qw);
    }
}
