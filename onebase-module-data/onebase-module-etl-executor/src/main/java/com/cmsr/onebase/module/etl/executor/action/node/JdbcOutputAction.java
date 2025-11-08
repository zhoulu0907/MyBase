package com.cmsr.onebase.module.etl.executor.action.node;

import com.cmsr.onebase.module.etl.executor.action.AbstractAction;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.executor.util.JooqUtil;
import org.apache.flink.connector.jdbc.core.table.JdbcConnectorOptions;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
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
 * @Date：2025/11/8 19:29
 */
public class JdbcOutputAction extends AbstractAction implements CreateTableAction, ExecuteSqlAction {

    @Override
    public void createTable() {
        JdbcOutputConfig config = (JdbcOutputConfig) node.getConfig();
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Field field : config.getTargetFields()) {
            DataType dataType = field.toFlinkTableType();
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
        tableEnv.createTable(node.getId(), tableDescriptor);
    }

    @Override
    public TableResult executeSql() {
        JdbcOutputConfig config = (JdbcOutputConfig) node.getConfig();
        org.jooq.Field[] intoFields = targetFieldNames(config);
        String intoTableName = config.getJdbcConfig().getTableName();
        org.jooq.Field[] fromFields = config.getFields().stream().map(f -> {
            String sourceFieldName = f.getSourceFieldName();
            return DSL.field(sourceFieldName);
        }).toList().toArray(new org.jooq.Field[0]);
        String fromTableName = graph.getSourceNode(node).getId();
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
