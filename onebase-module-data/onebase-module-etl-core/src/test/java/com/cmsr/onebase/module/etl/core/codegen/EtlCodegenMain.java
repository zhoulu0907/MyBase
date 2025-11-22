package com.cmsr.onebase.module.etl.core.codegen;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Author：huangjie
 * @Date：2025/8/14 17:59
 */
public class EtlCodegenMain {

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        dataSource.setUsername("postgres");
        dataSource.setPassword("onebase@2025");

        //创建配置内容
        GlobalConfig globalConfig = createGlobalConfigUseStyle();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);
        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();


        //设置根包
        //globalConfig.setBasePackage("com.cmsr.onebase.module.etl.core.dal.dataobject");

        globalConfig.enableTableDef();
        globalConfig.setTableDefPackage("com.cmsr.onebase.module.etl.core.dal.dataobject.table");
        //globalConfig.setTableDefClassPrefix("");
        globalConfig.setTableDefClassSuffix("TableDef");

        //设置表前缀和只生成哪些表
        //globalConfig.setTablePrefix("etl_");
        globalConfig.setGenerateTable("etl_catalog",
                "etl_datasource",
                "etl_execution_log",
                "etl_flink_function",
                "etl_flink_mapping",
                "etl_schedule_job",
                "etl_schema",
                "etl_table",
                "etl_workflow",
                "etl_workflow_table");

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityPackage("com.cmsr.onebase.module.etl.core.dal.dataobject");
        globalConfig.setEntitySuperClass(BaseBizEntity.class);
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityLombokAllArgsConstructorEnable(false);
        globalConfig.setEntityLombokNoArgsConstructorEnable(false);
        globalConfig.setEntityOverwriteEnable(true);

        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(17);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperXmlGenerateEnable(true);
        globalConfig.setMapperXmlPath("temp/xml");
        //设置生成 service
        globalConfig.setServiceGenerateEnable(false);
        globalConfig.setServiceImplGenerateEnable(false);
        //设置生成 controller
        globalConfig.setControllerGenerateEnable(false);
        globalConfig.setControllerRestStyle(false);


        globalConfig.setSourceDir("temp");
        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }


}
