package com.cmsr.onebase.framework.orm.repo;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/12/11 16:51
 */
public class QueryWrapperUtils {

    private static final String APPLICATION_ID = "application_id";

    private static final String VERSION_TAG = "version_tag";

    public static QueryColumn createApplicationIdColumn(ServiceImpl serviceImpl, QueryWrapper queryWrapper) {
        QueryTable queryTable = getQueryTable(queryWrapper);
        if (queryTable != null) {
            return new QueryColumn(queryTable, APPLICATION_ID);
        } else {
            return createApplicationIdColumn(serviceImpl);
        }
    }

    public static QueryColumn createVersionTagColumn(ServiceImpl serviceImpl, QueryWrapper queryWrapper) {
        QueryTable queryTable = getQueryTable(queryWrapper);
        if (queryTable != null) {
            return new QueryColumn(queryTable, VERSION_TAG);
        } else {
            return createVersionTagColumn(serviceImpl);
        }
    }

    public static QueryColumn createApplicationIdColumn(ServiceImpl serviceImpl) {
        String tableName = getTableName(serviceImpl.getMapper());
        if (StringUtils.isNotEmpty(tableName)) {
            return new QueryColumn(tableName, APPLICATION_ID);
        } else {
            return new QueryColumn(APPLICATION_ID);
        }
    }

    public static QueryColumn createVersionTagColumn(ServiceImpl serviceImpl) {
        String tableName = getTableName(serviceImpl.getMapper());
        if (StringUtils.isNotEmpty(tableName)) {
            return new QueryColumn(tableName, VERSION_TAG);
        } else {
            return new QueryColumn(VERSION_TAG);
        }
    }

    private static String getTableName(BaseMapper baseMapper) {
        Class<?> mapperClass = ClassUtil.getUsefulClass(baseMapper.getClass());
        Type type = mapperClass.getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            Class<?> modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            Table tableAnnotation = modelClass.getAnnotation(Table.class);
            if (tableAnnotation != null) {
                return tableAnnotation.value();
            }
        }
        return null;
    }


    public static boolean isQueryFilterable(QueryWrapper queryWrapper) {
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

    private static QueryTable getQueryTable(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (CollectionUtils.isNotEmpty(queryTables)) {
            return queryTables.get(0);
        }
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        if (whereQueryCondition != null) {
            QueryColumn queryColumn = whereQueryCondition.getColumn();
            if (queryColumn != null && queryColumn.getTable() != null) {
                return queryColumn.getTable();
            }
            if (whereQueryCondition instanceof Brackets brackets) {
                QueryCondition childCondition = brackets.getChildCondition();
                if (childCondition != null) {
                    if (childCondition.getColumn() != null && childCondition.getColumn().getTable() != null) {
                        return childCondition.getColumn().getTable();
                    }
                    QueryCondition nextCondition = CPI.getNextCondition(childCondition);
                    if (nextCondition != null && nextCondition.getColumn() != null && nextCondition.getColumn().getTable() != null) {
                        return nextCondition.getColumn().getTable();
                    }
                }
            }
        }
        return null;
    }


}
