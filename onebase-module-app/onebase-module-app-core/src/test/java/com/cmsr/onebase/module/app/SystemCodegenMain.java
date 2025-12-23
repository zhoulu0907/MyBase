package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SystemCodegenMain {
    //修改下面的各种参数
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "onebase@2025";

    private static final String[] tables = new String[]{
            "system_config",
            "system_corp",
            "system_corp_app_relation",
            "system_dept",
            "system_dict_data",
            "system_dict_type",
            "system_license",
            "system_login_log",
            "system_mail_account",
            "system_mail_log",
            "system_mail_template",
            "system_menu",
            "system_oauth2_access_token",
            "system_oauth2_approve",
            "system_oauth2_client",
            "system_oauth2_code",
            "system_oauth2_refresh_token",
            "system_operate_log",
            "system_post",
            "system_role",
            "system_role_menu",
            "system_sms_channel",
            "system_sms_code",
            "system_sms_log",
            "system_sms_template",
            "system_social_client",
            "system_social_user",
            "system_social_user_bind",
            "system_tenant",
            "system_tenant_admin",
            "system_tenant_package",
            "system_uid_worker_node",
            "system_user_app_relation",
            "system_user_post",
            "system_user_role",
            "system_users"
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
