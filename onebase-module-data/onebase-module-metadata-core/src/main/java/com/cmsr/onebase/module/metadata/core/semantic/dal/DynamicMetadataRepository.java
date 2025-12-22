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

import java.time.LocalDateTime;
import java.util.List;
import com.cmsr.onebase.framework.common.pojo.PageResult;

/**
 * 动态元数据通用数据访问仓库。
 *
 * 设计要点：
 * - 所有方法在执行前根据当前应用上下文切换业务数据源，执行结束后清理数据源上下文。
 * - 针对主键/外键等 ID 字段，统一在非空时转换为 Long 类型参与查询，避免字符串比较导致索引失效。
 * - 提供软删除（逻辑删除）与物理删除两类操作；查询方法可通过 filterDeleted 控制是否自动过滤逻辑删除记录。
 */
@Repository
@Slf4j
public class DynamicMetadataRepository {


    @Resource
    private UidGenerator uidGenerator;

    /**
     * 插入单条记录。
     * 会补齐系统字段（创建/更新时间、删除标记、乐观锁、所有者、部门等）。
     * @param tableName 目标表名
     * @param row       行数据
     * @return 影响行数
     */
    public int insert(String tableName, Row row) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            LocalDateTime now = LocalDateTime.now();
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

    /**
     * 批量插入记录。
     * 为每条记录补齐系统字段，避免循环中缺失必要字段。
     * @param tableName 目标表名
     * @param rows      行数据列表
     * @return 影响总行数
     */
    public int insertBatch(String tableName, List<Row> rows) {
        if (rows == null || rows.isEmpty()) { return 0; }
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            int count = 0;
            LocalDateTime now = LocalDateTime.now();
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

    /**
     * 根据条件更新记录。
     * 若未显式设置更新时间/更新人，则自动填充。
     * @param tableName 表名
     * @param row       更新内容
     * @param qw        条件包装器
     * @return 影响行数
     */
    public int updateByQuery(String tableName, Row row, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            LocalDateTime now = LocalDateTime.now();
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

    /**
     * 当字段缺失或值为空时设置默认值。
     * @param row  行数据
     * @param key  字段名
     * @param value 默认值
     */
    private void setIfMissingOrNull(Row row, String key, Object value) {
        if (!row.containsKey(key) || row.get(key) == null) { row.set(key, value); }
    }

    /**
     * ID 值规范化：非空时尝试转换为 Long 类型；
     * 转换失败记录警告日志并原样返回，以避免破坏调用方逻辑。
     * 空字符串或 null 返回 null。
     * @param value 输入值
     * @return Long 或原始值；空返回 null
     */
    private Object toLongIfNotEmpty(Object value) {
        if (value == null) { return null; }
        String s = String.valueOf(value).trim();
        if (s.isEmpty()) { return null; }
        try { return Long.parseLong(s); } catch (Exception e) { log.warn("Failed to parse id value to Long: {}", value); return value; }
    }

    /**
     * 批量 ID 规范化：将列表中非空可解析的元素转换为 Long；
     * 过滤掉无效/空元素，返回不可变空列表表示无有效 ID。
     * @param ids 原始 ID 列表
     * @return Long 列表或空列表
     */
    private List<?> toLongListIfNotEmpty(List<?> ids) {
        if (ids == null || ids.isEmpty()) { return List.of(); }
        List<Object> out = new java.util.ArrayList<>(ids.size());
        for (Object id : ids) {
            Object v = toLongIfNotEmpty(id);
            if (v != null) { out.add(v); }
        }
        return out;
    }

    /**
     * 软删除：将逻辑删除标志置为 1。
     * @param tableName 表名
     * @param qw        条件
     * @return 影响行数
     */
    public int softDeleteByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            Row update = new Row();
            update.put(SystemFieldConstants.OPTIONAL.DELETED, 1);
            return Db.updateByQuery(tableName, update, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 物理删除：按条件直接删除记录。
     * @param tableName 表名
     * @param qw        条件
     * @return 影响行数
     */
    public int deleteByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.deleteByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 按条件查询单条记录。
     * @param tableName 表名
     * @param qw        条件
     * @return 行数据；不存在时返回 null
     */
    public Row selectOneByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.selectOneByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 按条件查询多条记录。
     * @param tableName 表名
     * @param qw        条件
     * @return 行数据列表
     */
    public List<Row> selectListByQuery(String tableName, QueryWrapper qw) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 根据主键 ID 查询主表记录。
     * 非空时将 ID 规范化为 Long 参与查询；可选过滤逻辑删除。
     * @param tableName     表名
     * @param pkField       主键字段名
     * @param id            主键值
     * @param filterDeleted 是否过滤逻辑删除
     * @return 行数据；当 ID 为空或不可解析为数值时返回 null
     */
    public Row selectMainById(String tableName, String pkField, Object id, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            Object v = toLongIfNotEmpty(id);
            if (v == null) { return null; }
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(v));
            if (filterDeleted) { qw.and(new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0)); }
            return Db.selectOneByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 根据主键 ID 查询主表记录（默认过滤逻辑删除）。
     * @param tableName 表名
     * @param pkField   主键字段名
     * @param id        主键值
     * @return 行数据；当 ID 为空或不可解析为数值时返回 null
     */
    public Row selectMainById(String tableName, String pkField, Object id) {
        return selectMainById(tableName, pkField, id, true);
    }

    /**
     * 根据主键 ID 列表查询主表记录。
     * 将列表中有效 ID 统一转换为 Long 参与 IN 查询；可选过滤逻辑删除。
     * @param tableName     表名
     * @param pkField       主键字段名
     * @param ids           主键列表
     * @param filterDeleted 是否过滤逻辑删除
     * @return 结果列表；当列表为空或无有效 ID 返回空列表
     */
    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids, boolean filterDeleted) {
        if (ids == null || ids.isEmpty()) { return List.of(); }
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            List<?> v = toLongListIfNotEmpty(ids);
            if (v.isEmpty()) { return List.of(); }
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).in(v));
            if (filterDeleted) { qw.and(new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0)); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 根据主键 ID 列表查询主表记录（默认过滤逻辑删除）。
     * @param tableName 表名
     * @param pkField   主键字段名
     * @param ids       主键列表
     * @return 结果列表；当列表为空或无有效 ID 返回空列表
     */
    public List<Row> selectMainByIds(String tableName, String pkField, List<?> ids) {
        return selectMainByIds(tableName, pkField, ids, true);
    }

    /**
     * 查询子表：根据父记录 ID（parent_id）查询关联行（默认过滤逻辑删除）。
     * @param tableName 表名
     * @param parentId  父记录 ID
     * @return 结果列表；当 ID 为空或不可解析为数值时返回空列表
     */
    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId) {
        return selectSubtableRowsByParent(tableName, parentId, true);
    }

    /**
     * 查询子表：根据父记录 ID（parent_id）查询关联行。
     * @param tableName     表名
     * @param parentId      父记录 ID
     * @param filterDeleted 是否过滤逻辑删除
     * @return 结果列表；当 ID 为空或不可解析为数值时返回空列表
     */
    public List<Row> selectSubtableRowsByParent(String tableName, Object parentId, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            Object v = toLongIfNotEmpty(parentId);
            if (v == null) { return List.of(); }
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn("parent_id").eq(v));
            if (filterDeleted) { qw.and(new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0)); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 查询关系表：根据关系键与值查询关联行（默认过滤逻辑删除）。
     * @param tableName     表名
     * @param relationKey   关系键字段名（如某外键列）
     * @param relationValue 关系值
     * @return 结果列表；当值为空或不可解析为数值时返回空列表
     */
    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue) {
        return selectRelationRowsByParent(tableName, relationKey, relationValue, true);
    }

    /**
     * 查询关系表：根据关系键与值查询关联行。
     * @param tableName     表名
     * @param relationKey   关系键字段名（如某外键列）
     * @param relationValue 关系值
     * @param filterDeleted 是否过滤逻辑删除
     * @return 结果列表；当值为空或不可解析为数值时返回空列表
     */
    public List<Row> selectRelationRowsByParent(String tableName, String relationKey, Object relationValue, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            Object v = toLongIfNotEmpty(relationValue);
            if (v == null) { return List.of(); }
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(relationKey).eq(v));
            if (filterDeleted) { qw.and(new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0)); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 查询关系表：根据关系键与值查询关联行。
     * @param tableName     表名
     * @param relationKey   关系键字段名（如某外键列）
     * @param relationValue 关系值
     * @param filterDeleted 是否过滤逻辑删除
     * @return 结果列表；当值为空或不可解析为数值时返回空列表
     */
    public List<Row> selectRelationRowsByCondition(String tableName, String relationKey, Object relationValue, boolean filterDeleted) {
        ApplicationDataSourceManager.useBizDatasourceByAppId(ApplicationManager.getApplicationId());
        try {
            QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(relationKey).eq(relationValue));
            if (filterDeleted) { qw.and(new QueryColumn(SystemFieldConstants.OPTIONAL.DELETED).eq(0)); }
            return Db.selectListByQuery(tableName, qw);
        } finally {
            ApplicationDataSourceManager.clear();
        }
    }

    /**
     * 分页查询：先计算总数，再按偏移量与页大小查询数据。
     * @param tableName 表名
     * @param qw        条件
     * @param pageNo    页号（从 1 开始）
     * @param pageSize  页大小
     * @return 包含数据列表与总数的结果
     */
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
