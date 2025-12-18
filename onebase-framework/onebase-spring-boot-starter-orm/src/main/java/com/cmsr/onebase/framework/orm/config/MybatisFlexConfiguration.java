package com.cmsr.onebase.framework.orm.config;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.logicdelete.impl.TimeStampLogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    private static final Logger logger = LoggerFactory.getLogger("orm-sql");

    private static final String SNOWFLAKE_ID_GENERATOR = "snowflake_id";

    @Value("${mybatis-flex.print-sql:false}")
    private boolean printSql = false;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private DefaultEntityListener defaultEntityListener;

    @Override
    public void customize(FlexGlobalConfig defaultConfig) {
        // 不忽略任何条件，默认行为，容易隐藏深层次的问题
        QueryColumnBehavior.setIgnoreFunction(o -> false);
        // logic delete
        defaultConfig.setLogicDeleteColumn("deleted");
        LogicDeleteManager.setProcessor(new TimeStampLogicDeleteProcessor());

        Map<Class<?>, List<InsertListener>> insertListenerMap = new HashMap<>();
        insertListenerMap.put(BaseEntity.class, List.of(defaultEntityListener));
        insertListenerMap.put(WarmFlowBaseEntity.class, List.of(defaultEntityListener));

        Map<Class<?>, List<UpdateListener>> updateListenerMap = new HashMap<>();
        updateListenerMap.put(BaseEntity.class, List.of(defaultEntityListener));
        updateListenerMap.put(WarmFlowBaseEntity.class, List.of(defaultEntityListener));

        // base information listener
        defaultConfig.setEntityInsertListeners(insertListenerMap);
        defaultConfig.setEntityUpdateListeners(updateListenerMap);

        // key generators
        KeyGeneratorFactory.register(SNOWFLAKE_ID_GENERATOR, snowflakeIdGenerator);

        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(SNOWFLAKE_ID_GENERATOR);
        keyConfig.setBefore(true);

        defaultConfig.setKeyConfig(keyConfig);

        // SQL audit
        AuditManager.setAuditEnable(printSql);
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{}, Time consumption: {} ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );
    }
}
