package com.cmsr.onebase.framework.base.config;

import com.cmsr.onebase.framework.base.listener.BaseDOListener;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGenerators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class MyabtisFlexConfiguration {
    private static final Logger logger = LoggerFactory
            .getLogger("OneBase-SQL");

    static {
        // mybatis-flex configs
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();

        // SQL audit
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{}, Time consumption: {}ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );

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
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Auto);
        keyConfig.setValue(KeyGenerators.snowFlakeId);
        keyConfig.setBefore(true);

        defaultConfig.setKeyConfig(keyConfig);
    }
}
