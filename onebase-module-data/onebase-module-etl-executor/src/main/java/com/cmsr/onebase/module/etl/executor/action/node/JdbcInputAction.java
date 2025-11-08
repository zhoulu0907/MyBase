package com.cmsr.onebase.module.etl.executor.action.node;

import com.cmsr.onebase.module.etl.executor.action.AbstractAction;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import org.apache.flink.connector.jdbc.core.table.JdbcConnectorOptions;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.types.DataType;

/**
 * @Author：huangjie
 * @Date：2025/11/8 18:08
 */
public class JdbcInputAction extends AbstractAction implements CreateTableAction {

    @Override
    public void createTable() {
        JdbcInputConfig config = (JdbcInputConfig) node.getConfig();
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Field field : config.getFields()) {
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
}
