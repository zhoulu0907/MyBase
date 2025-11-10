package com.cmsr.onebase.module.etl.executor.graph.node;

import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.common.graph.conf.Field;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.executor.util.FlinkUtil;
import com.cmsr.onebase.module.etl.executor.util.JooqUtil;
import lombok.ToString;
import org.apache.flink.connector.jdbc.core.table.JdbcConnectorOptions;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.types.DataType;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.util.List;

import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

/**
 * @Author：huangjie
 * @Date：2025/11/9 7:44
 */
@ToString(callSuper = true)
public class JdbcOutputNode extends Node<JdbcOutputConfig> implements CreateTableAction, ExecuteSqlAction {

    @Override
    public void createTable(TableEnvironment tableEnv, WorkflowGraph graph) {
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Field field : config.getTargetFields()) {
            DataType dataType = FlinkUtil.toFlinkTableType(field);
            schemaBuilder.column(field.getFieldName(), dataType);
        }
        TableDescriptor tableDescriptor = TableDescriptor.forConnector("jdbc")
                .schema(schemaBuilder.build())
                .option(JdbcConnectorOptions.DRIVER, config.getJdbcConfig().getDriver())
                .option(JdbcConnectorOptions.URL, config.getJdbcConfig().getJdbcUrl())
                .option(JdbcConnectorOptions.USERNAME, config.getJdbcConfig().getUsername())
                .option(JdbcConnectorOptions.PASSWORD, config.getJdbcConfig().getPassword())
                .option(JdbcConnectorOptions.TABLE_NAME, config.getJdbcConfig().getTableName())
                .build();
        tableEnv.createTable(getId(), tableDescriptor);
    }

    @Override
    public TableResult executeSql(TableEnvironment tableEnv, WorkflowGraph graph) {
        org.jooq.Field[] intoFields = targetFieldNames(config);
        String intoTableName = config.getJdbcConfig().getTableName();
        org.jooq.Field[] fromFields = config.getFields().stream().map(f -> {
            String sourceFieldName = f.getSourceFieldName();
            return DSL.field(sourceFieldName);
        }).toList().toArray(new org.jooq.Field[0]);
        String fromTableName = graph.getSourceNode(this).getId();
        InsertOnDuplicateStep<Record> select = JooqUtil.DSL_CONTEXT
                .insertInto(table(intoTableName)).columns(intoFields)
                .select(select(fromFields).from(table(fromTableName)));
        String sql = select.getSQL(ParamType.INLINED);
        return tableEnv.executeSql(sql);
    }


    private org.jooq.Field[] targetFieldNames(JdbcOutputConfig config) {
        return config.getFields().stream().map(f -> {
            String targetFieldId = f.getTargetFieldId();
            String targetFieldName = findFieldName(config.getTargetFields(), targetFieldId);
            return DSL.field(targetFieldName);
        }).toList().toArray(new org.jooq.Field[0]);
    }

    private String findFieldName(List<Field> fields, String fieldId) {
        for (Field field : fields) {
            if (field.getFieldId().equals(fieldId)) {
                return field.getFieldName();
            }
        }
        throw new IllegalArgumentException("fieldId not found: " + fieldId);
    }
}
