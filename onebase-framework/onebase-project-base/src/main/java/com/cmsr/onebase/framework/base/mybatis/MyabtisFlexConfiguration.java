package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class MyabtisFlexConfiguration implements MyBatisFlexCustomizer {

    private static final Logger logger = LoggerFactory.getLogger("OneBase-SQL");

    private static final String SNOWFLAKE_UUID = "custom_snowflake";

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Override
    public void customize(FlexGlobalConfig defaultConfig) {
        // logic delete
        defaultConfig.setLogicDeleteColumn("deleted");
        defaultConfig.setNormalValueOfLogicDelete(0);
        defaultConfig.setDeletedValueOfLogicDelete(System.currentTimeMillis());

//        // setVersionColumn
//        defaultConfig.setVersionColumn("lock_version");

        // tenant column
        defaultConfig.setTenantColumn("tenant_id");

        // base infomation listener
        defaultConfig.setEntityInsertListeners(Map.of(
                BaseDO.class, List.of(new BaseDOListener())
        ));
        defaultConfig.setEntityUpdateListeners(Map.of(
                BaseDO.class, List.of(new BaseDOListener())
        ));

        // key generators
        KeyGeneratorFactory.register(SNOWFLAKE_UUID, snowflakeGenerator);

        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(SNOWFLAKE_UUID);
        keyConfig.setBefore(true);

        defaultConfig.setKeyConfig(keyConfig);

        // SQL audit
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{}, Time consumption: {}ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );
    }
}
