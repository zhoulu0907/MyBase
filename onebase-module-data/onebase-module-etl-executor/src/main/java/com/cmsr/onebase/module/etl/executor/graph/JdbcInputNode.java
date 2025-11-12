package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.Field;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.util.FlinkUtil;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.jdbc.core.table.JdbcConnectorOptions;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.types.DataType;

/**
 * @Author：huangjie
 * @Date：2025/11/9 7:39
 */
@Slf4j
@ToString(callSuper = true)
public class JdbcInputNode extends Node<JdbcInputConfig> implements CreateTableAction {

    @Override
    public void createTable(TableEnvironment tableEnv, WorkflowGraph graph) {
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Field field : config.getFields()) {
            DataType dataType = FlinkUtil.toFlinkTableType(field.getFieldType(), field.getLength(), field.getPrecision(), field.getScale());
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
        log.info("create table: {}, {}", getId(), tableDescriptor);
        tableEnv.createTable(getId(), tableDescriptor);
    }
}
