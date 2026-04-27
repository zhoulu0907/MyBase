package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.UnionConfig;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import com.cmsr.onebase.module.etl.executor.util.JooqUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectUnionStep;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

/**
 * @Author：huangjie
 * @Date：2025/11/19 11:28
 */
@Slf4j
@ToString(callSuper = true)
public class UnionNode extends Node<UnionConfig> implements SqlQueryAction {


    @Override
    public Table sqlQuery(TableEnvironment tableEnv, WorkflowGraph graph) {
        List<UnionConfig.ColumnMapping> columnMappings = this.config.getColTitles();
        Set<String> finalColumnSet = columnMappings.stream().map(UnionConfig.ColumnMapping::getFieldName).collect(Collectors.toSet());
        Map<String, String> typeRef = new HashMap<>();
        for (UnionConfig.ColumnMapping columnMapping : columnMappings) {
            if (typeRef.containsKey(columnMapping.getFieldName())) {
                continue;
            }
            typeRef.put(columnMapping.getFieldName(), columnMapping.getFieldType());
        }
        // <nodeId, <alias, field>>
        Map<String, Map<String, String>> nodeColumns = new HashMap<>();
        for (UnionConfig.ColumnMapping columnMapping : columnMappings) {
            String[] fieldFqn = StringUtils.split(columnMapping.getFieldFqn(), ".");
            String nodeId = fieldFqn[0];
            String originField = fieldFqn[1];
            String fieldAlias = columnMapping.getFieldName();
            if (!nodeColumns.containsKey(nodeId)) {
                nodeColumns.put(nodeId, new HashMap<>());
            }
            nodeColumns.get(nodeId).put(fieldAlias, originField);
        }

        for (String nodeId : nodeColumns.keySet()) {
            Map<String, String> aliasMapping = nodeColumns.get(nodeId);
            for (String alias : finalColumnSet) {
                if (!aliasMapping.containsKey(alias)) {
                    aliasMapping.put(alias, null);
                }
            }
        }
        var selectStep = buildUnionSql(nodeColumns, typeRef);
        String sql = selectStep.getSQL();

        log.info("execute sql: {}", sql);
        return tableEnv.sqlQuery(sql);
    }

    private SelectUnionStep<Record> buildUnionSql(Map<String, Map<String, String>> nodeColumns, Map<String, String> typeRef) {
        int size = nodeColumns.size();
        Set<String> nodeIdSet = nodeColumns.keySet();
        Iterator<String> nodeIdIter = nodeIdSet.iterator();
        String nodeId = nodeIdIter.next();
        SelectUnionStep<Record> unionStep = JooqUtil.DSL_CONTEXT.select(
                buildUnionColumns(nodeColumns.get(nodeId), typeRef)
        ).from(nodeId);
        while (nodeIdIter.hasNext()) {
            String nextNodeId = nodeIdIter.next();
            buildUnionSql(unionStep, nextNodeId, nodeColumns.get(nextNodeId), typeRef);
        }
        return unionStep;
    }

    private Field[] buildUnionColumns(Map<String, String> columnAlias, Map<String, String> typeRef) {
        Field[] fields = new Field[columnAlias.size()];
        int index = 0;
        for (String alias : columnAlias.keySet()) {
            String originColumn = columnAlias.get(alias);
            if (originColumn == null) {
                fields[index] = interferNullCastInterfer(typeRef.get(alias)).as(alias);
            } else {
                fields[index] = field(originColumn, Object.class).as(alias);
            }
            index++;
        }
        return fields;
    }

    private Field<?> interferNullCastInterfer(String dataType) {
        Field<?> field;
        switch (dataType) {
            case "float", "double", "decimal": {
                field = castNull(BigDecimal.class);
                break;
            }
            case "bigint", "integer", "smallint", "tinyint": {
                field = castNull(Long.class);
                break;
            }
            case "timestamp", "date", "time": {
                field = castNull(LocalDateTime.class);
                break;
            }
            case "boolean": {
                field = castNull(Boolean.class);
                break;
            }
            default: {
                field = castNull(String.class);
            }
        }
        return field;
    }

    private SelectUnionStep<Record> buildUnionSql(SelectUnionStep<Record> unionStep, String nodeId,
                                                  Map<String, String> columns, Map<String, String> typeRef) {
        return unionStep.unionAll(
                select(buildUnionColumns(columns, typeRef)).from(nodeId)
        );
    }

}
