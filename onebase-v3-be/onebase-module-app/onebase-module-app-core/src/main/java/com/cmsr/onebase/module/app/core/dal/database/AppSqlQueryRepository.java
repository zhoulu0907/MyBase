package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.mapper.AppSqlQueryMapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.DbChain;
import com.mybatisflex.core.row.Row;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:58
 */
@Setter
@Repository
public class AppSqlQueryRepository {

    @Autowired
    private AppSqlQueryMapper appSqlQueryMapper;

    public List<Long> findDeptHierarchyByUserId(Long userId) {
        List<Long> result = new ArrayList<>();
        Long currentDeptId = null;
        {
            String sql = """
                    select
                    	dept_id
                    from
                    	system_users
                    where
                    	deleted = 0 and id = ?
                    """;
            Row row = Db.selectOneBySql(sql, userId);
            currentDeptId = (row == null ? null : row.getLong("dept_id"));
            if (currentDeptId != null) {
                result.add(currentDeptId);
            }
        }
        int loopCount = 0;
        while (currentDeptId != null && currentDeptId > 0) {
            currentDeptId = null;
            String sql = """
                    select
                    	id,
                    	parent_id
                    from
                    	system_dept
                    where
                    	deleted = 0 and id = ?
                    """;
            Row row = Db.selectOneBySql(sql, currentDeptId);
            currentDeptId = (row == null ? null : row.getLong("parent_id"));
            if (currentDeptId != null && currentDeptId > 0) {
                result.add(currentDeptId);
            }
            loopCount++;
            if (loopCount > 100) {
                throw new RuntimeException("findDeptHierarchyByUserId 部门层级过深");
            }
        }
        return result;
    }

    private List<Long> findAllUserIdsByDeptIds(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();

        List<Row> dataSet = DbChain.table("system_users")
                .select("id")
                .eq("deleted", 0)
                .in("dept_id", deptIds)
                .list();
        for (Row dataRow : dataSet) {
            result.add(dataRow.getLong("id"));
        }
        return result;
    }

    public List<Long> findAllUserIdsByDeptIds(Long deptId, Integer isIncludeChild) {
        if (deptId == null || deptId <= 0) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        {
            List<Long> ids = findAllUserIdsByDeptIds(List.of(deptId));
            result.addAll(ids);
        }
        if (isIncludeChild != null && isIncludeChild == 1) {
            List<Long> deptIds = List.of(deptId);
            int loopCount = 0;
            while (CollectionUtils.isNotEmpty(deptIds)) {
                List<Row> dataSet = DbChain.table("system_dept")
                        .select("id")
                        .eq("deleted", 0)
                        .in("parent_id", deptIds)
                        .list();
                deptIds = new ArrayList<>();
                for (Row dataRow : dataSet) {
                    Long id = dataRow.getLong("id");
                    if (id != null && id > 0) {
                        deptIds.add(id);
                    }
                }
                List<Long> ids = findAllUserIdsByDeptIds(deptIds);
                result.addAll(ids);
                //
                loopCount++;
                if (loopCount > 100) {
                    throw new RuntimeException("findAllUserIdsByDeptIds部门层级过深");
                }
            }
        }
        return result;
    }

}
