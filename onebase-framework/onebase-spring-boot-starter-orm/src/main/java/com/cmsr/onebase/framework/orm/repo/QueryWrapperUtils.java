package com.cmsr.onebase.framework.orm.repo;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.mybatisflex.core.query.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/12/11 16:51
 */
public class QueryWrapperUtils {

    public static boolean isQueryFilterable(QueryWrapper queryWrapper) {
        if (ApplicationManager.isIgnoreApplicationCondition() && ApplicationManager.isIgnoreVersionTagCondition()) {
            return false;
        }
        // 不处理UNION类型
        List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
        if (CollectionUtils.isNotEmpty(unions)) {
            return false;
        }
        // 不处理子查询
        List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
        if (CollectionUtils.isNotEmpty(childSelect)) {
            return false;
        }
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (CollectionUtils.isNotEmpty(joinTables)) {
            return false;
        }
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtils.isNotEmpty(joins)) {
            return false;
        }
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (CollectionUtils.isNotEmpty(queryTables) && queryTables.size() > 1) {
            return false;
        }
        // 需要处理
        return true;
    }

    public static QueryTable getQueryTable(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (CollectionUtils.isNotEmpty(queryTables)) {
            return queryTables.get(0);
        }
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        if (whereQueryCondition != null) {
            return whereQueryCondition.getColumn().getTable();
        }
        return null;
    }

}
