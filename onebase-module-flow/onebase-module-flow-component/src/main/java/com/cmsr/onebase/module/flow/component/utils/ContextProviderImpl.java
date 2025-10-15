package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/11 21:33
 */
@Setter
@Component
public class ContextProviderImpl implements InitializingBean {

    public static final String EXECUTE_CONTEXT = "executeContext:";
    public static final String VARIABLE_CONTEXT = "variableContext:";

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void afterPropertiesSet() {
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(redisConnectionFactory);
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.afterPropertiesSet();
    }

    public void storeExecuteContext(String executionUuid, ExecuteContext executeContext) {
        redisTemplate.opsForValue().set(EXECUTE_CONTEXT + executionUuid, executeContext, 1, TimeUnit.HOURS);
    }

    public void storeVariableContext(String executionUuid, VariableContext variableContext) {
        redisTemplate.opsForValue().set(VARIABLE_CONTEXT + executionUuid, variableContext, 1, TimeUnit.HOURS);
    }

    public ExecuteContext restoreExecuteContext(String executionUuid) {
        ExecuteContext deserialize = (ExecuteContext) redisTemplate.opsForValue().get(EXECUTE_CONTEXT + executionUuid);
        return deserialize;
    }

    public VariableContext restoreVariableContext(String executionUuid) {
        VariableContext deserialize = (VariableContext) redisTemplate.opsForValue().get(VARIABLE_CONTEXT + executionUuid);
        return deserialize;
    }

}
