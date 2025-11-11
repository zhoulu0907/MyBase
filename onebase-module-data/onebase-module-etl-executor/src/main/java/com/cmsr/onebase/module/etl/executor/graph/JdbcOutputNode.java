package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputMapper;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.util.FlinkUtil;
import com.cmsr.onebase.module.etl.executor.util.JooqUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
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

import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

/**
 * @Author：huangjie
 * @Date：2025/11/9 7:44
 */
@Slf4j
@ToString(callSuper = true)
public class JdbcOutputNode extends Node<JdbcOutputConfig> implements CreateTableAction, ExecuteSqlAction {

    @Override
    public void createTable(TableEnvironment tableEnv, WorkflowGraph graph) {
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (JdbcOutputMapper field : config.getFields()) {
            DataType dataType = FlinkUtil.toFlinkTableType(field.getTargetFieldType(), field.getTargetFieldLength(), field.getTargetFieldPrecision(), field.getTargetFieldScale());
            schemaBuilder.column(field.getTargetFieldName(), dataType);
        }
        TableDescriptor tableDescriptor = TableDescriptor.forConnector("jdbc")
                .schema(schemaBuilder.build())
                .option(JdbcConnectorOptions.DRIVER, config.getJdbcConfig().getDriver())
                .option(JdbcConnectorOptions.URL, config.getJdbcConfig().getJdbcUrl())
                .option(JdbcConnectorOptions.USERNAME, config.getJdbcConfig().getUsername())
                .option(JdbcConnectorOptions.PASSWORD, config.getJdbcConfig().getPassword())
                .option(JdbcConnectorOptions.TABLE_NAME, config.getJdbcConfig().getTableName())
                .build();
        log.info("create table: {}, {}", getId(), tableDescriptor);
        tableEnv.createTable(getId(), tableDescriptor);
    }

    @Override
    public TableResult executeSql(TableEnvironment tableEnv, WorkflowGraph graph) {
        org.jooq.Field[] intoFields = intoFieldNames(config);
        String intoTableName = getId();
        org.jooq.Field[] fromFields = fromFieldNames(config);
        String fromTableName = graph.findIncomingNode(this).getId();
        InsertOnDuplicateStep<Record> select = JooqUtil.DSL_CONTEXT
                .insertInto(table(intoTableName)).columns(intoFields)
                .select(select(fromFields).from(table(fromTableName)));
        String sql = select.getSQL(ParamType.INLINED);
        log.info("execute sql: {}", sql);
        return tableEnv.executeSql(sql);
    }


    private org.jooq.Field[] intoFieldNames(JdbcOutputConfig config) {
        return config.getFields().stream().map(f -> {
            String targetFieldName = f.getTargetFieldName();
            return DSL.field(targetFieldName);
        }).toList().toArray(new org.jooq.Field[0]);
    }


    private org.jooq.Field[] fromFieldNames(JdbcOutputConfig config) {
        return config.getFields().stream().map(f -> {
            String sourceFieldName = f.getSourceFieldName();
            return DSL.field(sourceFieldName);
        }).toList().toArray(new org.jooq.Field[0]);
    }

}
