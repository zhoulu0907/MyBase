package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.PairJoinConfig;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import com.cmsr.onebase.module.etl.executor.util.JooqUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.impl.DSL;

import static org.jooq.impl.DSL.*;

/**
 * @Author：huangjie
 * @Date：2025/11/19 11:28
 */
@Slf4j
@ToString(callSuper = true)
public class PairJoinNode extends Node<PairJoinConfig> implements SqlQueryAction {

    @Override
    public Table sqlQuery(TableEnvironment tableEnv, WorkflowGraph graph) {
        org.jooq.Field[] selectFields = selectFieldNames();
        JoinType joinType = getJoinType();

        var select = JooqUtil.DSL_CONTEXT.select(selectFields)
                .from(table(config.getLeftNodeId()))
                .join(table(config.getRightNodeId()), joinType)
                .on(and(joinConditions()));
        String sql = select.getSQL();
        log.info("execute sql: {}", sql);
        return tableEnv.sqlQuery(sql);
    }

    private JoinType getJoinType() {
        JoinType joinType;
        if ("full".equals(config.getJoinType())) {
            joinType = JoinType.FULL_OUTER_JOIN;
        } else if ("left".equals(config.getJoinType())) {
            joinType = JoinType.LEFT_OUTER_JOIN;
        } else if ("right".equals(config.getJoinType())) {
            joinType = JoinType.RIGHT_OUTER_JOIN;
        } else if ("inner".equals(config.getJoinType())) {
            joinType = JoinType.JOIN;
        } else {
            throw new RuntimeException("join type error: " + config.getJoinType());
        }
        return joinType;
    }

    private Field[] selectFieldNames() {
        return config.getMappings().stream()
                .map(mapping -> DSL.field(
                        name(mapping.getNodeId(), mapping.getFieldName())).as(mapping.getUpdatedFieldName()))
                .toArray(Field[]::new);
    }

    private Condition[] joinConditions() {
        return config.getFieldPairs().stream()
                .map(fieldPair -> {
                    String[] left = StringUtils.split(fieldPair.getLeftFieldFqn(), ".");
                    String[] right = StringUtils.split(fieldPair.getRightFieldFqn(), ".");
                    Field<Object> leftField = field(name(config.getLeftNodeId(), left[1]), Object.class);
                    Field<Object> rightField = field(name(config.getRightNodeId(), right[1]), Object.class);
                    return DSL.condition(leftField.eq(rightField));
                })
                .toArray(Condition[]::new);
    }


}
