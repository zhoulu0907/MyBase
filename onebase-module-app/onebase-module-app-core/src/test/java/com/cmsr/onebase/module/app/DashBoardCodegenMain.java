package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DashBoardCodegenMain {
    //修改下面的各种参数
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "onebase@2025";

    private static final String[] tables = new String[]{
            "dashboard_template",
    };
    // com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO
    private static String basePackage = "com.cmsr.onebase.module.system.dal";

    private static String sourceDir = "database/temp";


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
        globalConfig.setEntityJdkVersion(17);
        globalConfig.setEntityOverwriteEnable(true);
        //生成Mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperPackage(basePackage + ".mapper");
        globalConfig.setMapperOverwriteEnable(true);
        //
        globalConfig.setMapperXmlGenerateEnable(true);
        globalConfig.setMapperXmlOverwriteEnable(true);
        globalConfig.setMapperXmlPath(sourceDir);
        globalConfig.setMapperXmlOverwriteEnable(true);
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
        globalConfig.setTableDefOverwriteEnable(true);
        globalConfig.setTableDefPackage(basePackage + ".dataobject.table");
        globalConfig.setEntityOverwriteEnable(true);
        //生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

}
