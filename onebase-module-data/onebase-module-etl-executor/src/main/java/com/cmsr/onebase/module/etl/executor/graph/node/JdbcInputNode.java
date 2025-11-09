package com.cmsr.onebase.module.etl.executor.graph.node;

import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.util.FlinkTypeUtil;
import lombok.Data;
import org.apache.flink.connector.jdbc.core.table.JdbcConnectorOptions;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.types.DataType;

@Data
public class JdbcInputNode extends Node implements CreateTableAction {

    private JdbcInputConfig config;

    @Override
    public void createTable(TableEnvironment tableEnv) {
        Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Field field : config.getFields()) {
            DataType dataType = FlinkTypeUtil.getFlinkTableType(field.getFieldType(), field.getLength(), field.getPrecision(), field.getScale());
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

}
