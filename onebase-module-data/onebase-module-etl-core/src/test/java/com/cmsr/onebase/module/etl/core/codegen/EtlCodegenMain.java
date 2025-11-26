package com.cmsr.onebase.module.etl.core.codegen;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Author：huangjie
 * @Date：2025/8/14 17:59
 */
public class EtlCodegenMain {

    //修改下面的各种参数
//    private static final String DB_DRIVER = "org.postgresql.Driver";
//    private static final String DB_URL = "jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3";
//    private static final String DB_USERNAME = "postgres";
//    private static final String DB_PASSWORD = "onebase@2025";
    private static final String DB_DRIVER = "dm.jdbc.driver.DmDriver";
    private static final String DB_URL = "jdbc:dm://10.0.104.50:5237/ONEBASE_CLOUD_V3?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8&schema=ONEBASE_CLOUD_V3";
    private static final String DB_USERNAME = "ONEBASE_CLOUD_V3";
    private static final String DB_PASSWORD = "Onebase2025";

    private static final String[] tables = new String[]{
            "etl_catalog",
            "etl_datasource",
            "etl_execution_log",
            "etl_flink_function",
            "etl_flink_mapping",
            "etl_schedule_job",
            "etl_schema",
            "etl_table",
            "etl_workflow",
            "etl_workflow_table"
    };

    private static String entityPackage = "com.cmsr.onebase.module.etl.core.dal.dataobject";
    private static String tableDefPackage = "com.cmsr.onebase.module.etl.core.dal.dataobject.table";

    private static String sourceDir = "d:/temp";


    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(DB_DRIVER);
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        //
        createEntity(dataSource);
        createTableDef(dataSource);
    }

    private static void createEntity(HikariDataSource dataSource) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGenerateTable(tables);
        globalConfig.setEntityPackage(entityPackage);
        globalConfig.setEntitySuperClass(BaseBizEntity.class);
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityLombokAllArgsConstructorEnable(false);
        globalConfig.setEntityLombokNoArgsConstructorEnable(false);
        globalConfig.setEntityOverwriteEnable(true);
        globalConfig.setEntityJdkVersion(17);
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperXmlGenerateEnable(true);
        globalConfig.setMapperXmlPath(sourceDir);
        //生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

    private static void createTableDef(HikariDataSource dataSource) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGenerateTable(tables);
        globalConfig.enableTableDef();
        globalConfig.setTableDefPackage(tableDefPackage);
        globalConfig.setSourceDir(sourceDir);
        //生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }


}
