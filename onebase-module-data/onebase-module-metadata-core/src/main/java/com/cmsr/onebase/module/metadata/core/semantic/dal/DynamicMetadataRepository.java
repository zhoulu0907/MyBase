package com.cmsr.onebase.module.metadata.core.semantic.dal;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.config.ApplicationDataSourceManager;

import org.springframework.stereotype.Repository;
import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import com.esotericsoftware.kryo.kryo5.minlog.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.cmsr.onebase.framework.common.pojo.PageResult;

@Repository
@Slf4j
public class DynamicMetadataRepository {


    @Resource
    private UidGenerator uidGenerator;

    public int insert(String tableName, Row row) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            Long userDeptId = SecurityFrameworkUtils.getLoginUserDeptId() != null ? SecurityFrameworkUtils.getLoginUserDeptId() : 0L;

            setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.CREATED_TIME, now);
            setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.UPDATED_TIME, now);
            setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.DELETED, 0);
            setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.LOCK_VERSION, 0);

            if (userId != null) {
                setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.OWNER_ID, userId);
                setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.CREATOR, userId);
                setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.UPDATER, userId);
            }

            if (userDeptId != null) {
                setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.OWNER_DEPT, userDeptId);
            }

            return Db.insert(tableName, row);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public int insertBatch(String tableName, List<Row> rows) {
        if (rows == null || rows.isEmpty()) { return 0; }
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            int count = 0;
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            Long userDeptId = SecurityFrameworkUtils.getLoginUserDeptId() != null ? SecurityFrameworkUtils.getLoginUserDeptId() : 0L;
            for (Row row : rows) {
                setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.CREATED_TIME, now);
                setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.UPDATED_TIME, now);
                setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.DELETED, 0);
                setIfMissingOrNull(row, SystemFieldConstants.OPTIONAL.LOCK_VERSION, 0);
                if (userId != null) {
                    setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.OWNER_ID, userId);
                    setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.CREATOR, userId);
                    setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.UPDATER, userId);
                }
                if (userDeptId != null) {
                    setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.OWNER_DEPT, userDeptId);
                }
                count += Db.insert(tableName, row);
            }
            return count;
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public int updateByQuery(String tableName, Row row, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            if (!row.containsKey(SystemFieldConstants.OPTIONAL.UPDATED_TIME) || row.get(SystemFieldConstants.OPTIONAL.UPDATED_TIME) == null) { row.set(SystemFieldConstants.OPTIONAL.UPDATED_TIME, now); }
            if (userId != null) {
                setIfMissingOrNull(row, SystemFieldConstants.REQUIRE.UPDATER, userId);
            }
            return Db.updateByQuery(tableName, row, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    private void setIfMissingOrNull(Row row, String key, Object value) {
        if (!row.containsKey(key) || row.get(key) == null) { row.set(key, value); }
    }

    public int softDeleteByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            Row update = new Row();
            update.put("deleted", 1);
            return Db.updateByQuery(tableName, update, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public int deleteByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.deleteByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public Row selectOneByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.selectOneByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public List<Row> selectListByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public Row selectMainById(String tableName, String pkField, Object id, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(String.valueOf(id)));
            if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
            return Db.selectOneByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public Row selectMainById(String tableName, String pkField, Object id) {
        return selectMainById(tableName, pkField, id, true);
    }

    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids, boolean filterDeleted) {
        if (ids == null || ids.isEmpty()) { return List.of(); }
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).in(ids));
            if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids) {
        return selectMainByIds(tableName, pkField, ids, true);
    }

    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId) {
        return selectSubtableRowsByParent(tableName, parentId, true);
    }

    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn("parent_id").eq(String.valueOf(parentId)));
            if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue) {
        return selectRelationRowsByParent(tableName, relationKey, relationValue, true);
    }

    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(relationKey).eq(String.valueOf(relationValue)));
            if (filterDeleted) { qw.and(new QueryColumn("deleted").eq("0")); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    public PageResult<Row> selectPageByQuery(String tableName, QueryWrapper qw, int pageNo, int pageSize) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper countQw = QueryWrapper.create().where(CPI.getWhereQueryCondition(qw));
            countQw.select(QueryMethods.count().as("total"));
            Row countRow = Db.selectOneByQuery(tableName, countQw);
            long total = 0L;
            if (countRow != null) {
                Object tv = countRow.get("total");
                if (tv != null) {
                    try { total = Long.parseLong(String.valueOf(tv)); } catch (Exception ignored) {}
                }
            }
            int offset = Math.max(0, (pageNo - 1) * pageSize);
            qw.limit(offset, pageSize);
            List<Row> rows = Db.selectListByQuery(tableName, qw);
            return new PageResult<>(rows, total);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }
}
