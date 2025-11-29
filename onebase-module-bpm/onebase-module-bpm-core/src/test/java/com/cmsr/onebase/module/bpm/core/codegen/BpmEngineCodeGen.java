package com.cmsr.onebase.module.bpm.core.codegen;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class BpmEngineCodeGen {
    //修改下面的各种参数
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "onebase@2025";

    private static final String[] tables = new String[]{
            "bpm_flow_definition",
            "bpm_flow_node",
            "bpm_flow_skip",
            "bpm_flow_instance",
            "bpm_flow_task",
            "bpm_flow_his_task",
            "bpm_flow_user"
    };

    private static String basePackage = "com.cmsr.onebase.module.engine.orm.mybatisflex";

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
        globalConfig.setSourceDir(sourceDir);
        //生成实体
        globalConfig.setEntityPackage(basePackage + ".dataobject");
        globalConfig.setEntityClassSuffix("DO");
        globalConfig.setEntitySuperClass(BaseEntity.class);
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityLombokAllArgsConstructorEnable(false);
        globalConfig.setEntityLombokNoArgsConstructorEnable(false);
        globalConfig.setEntityOverwriteEnable(true);
        globalConfig.setEntityJdkVersion(17);
        //生成Mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperPackage(basePackage + ".mapper");
        globalConfig.setMapperOverwriteEnable(true);
        //
        globalConfig.setMapperXmlGenerateEnable(true);
        globalConfig.setMapperXmlPath(sourceDir);
        //生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

    private static void createTableDef(HikariDataSource dataSource) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGenerateTable(tables);
        globalConfig.setSourceDir(sourceDir);
        //生成表定义
        globalConfig.enableTableDef();
        globalConfig.setTableDefPackage(basePackage + ".dataobject.table");
        //生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

}
