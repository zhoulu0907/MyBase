package com.cmsr.onebase.framework.orm.config;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Setter
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    private static final Logger logger = LoggerFactory.getLogger("orm-sql");

    private static final String SNOWFLAKE_ID_GENERATOR = "snowflake_id";

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void customize(FlexGlobalConfig defaultConfig) {
        // logic delete
        defaultConfig.setLogicDeleteColumn("deleted");
        defaultConfig.setNormalValueOfLogicDelete(0);
        defaultConfig.setDeletedValueOfLogicDelete(System.currentTimeMillis());

//        // setVersionColumn
//        defaultConfig.setVersionColumn("lock_version");

        // base infomation listener
        defaultConfig.setEntityInsertListeners(Map.of(
                BaseEntity.class, List.of(new DefaultEntityListener())
        ));
        defaultConfig.setEntityUpdateListeners(Map.of(
                BaseEntity.class, List.of(new DefaultEntityListener())
        ));

        // key generators
        KeyGeneratorFactory.register(SNOWFLAKE_ID_GENERATOR, snowflakeIdGenerator);

        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(SNOWFLAKE_ID_GENERATOR);
        keyConfig.setBefore(true);

        defaultConfig.setKeyConfig(keyConfig);

        // SQL audit
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{}, Time consumption: {} ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );
    }
}
