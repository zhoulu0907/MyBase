package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.orm.data.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class AppCodegenMain {
    //修改下面的各种参数
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "onebase@2025";

    private static final String[] tables = new String[]{
            "app_application",
            "app_application_tag",
            "app_auth_data_group",
            "app_auth_field",
            "app_auth_permission",
            "app_auth_role",
            "app_auth_role_dept",
            "app_auth_role_user",
            "app_auth_view",
            "app_menu",
            "app_resource_component",
            "app_resource_page",
            "app_resource_page_metadata",
            "app_resource_page_ref_router",
            "app_resource_pageset",
            "app_resource_pageset_label",
            "app_resource_pageset_page",
            "app_resource_workbench_component",
            "app_resource_workbench_page",
            "app_tag",
            "app_version",
            "app_version_resource"
    };

    private static String basePackage = "com.cmsr.onebase.module.app.core.dal";

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
