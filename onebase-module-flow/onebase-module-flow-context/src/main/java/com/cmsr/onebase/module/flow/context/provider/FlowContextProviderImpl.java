package com.cmsr.onebase.module.flow.context.provider;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import lombok.Setter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.Kryo5Codec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/11 21:33
 */
@Setter
@Component
public class FlowContextProviderImpl implements FlowContextProvider {

    public static final String EXECUTE_CONTEXT = "flow:executeContext:";
    public static final String VARIABLE_CONTEXT = "flow:variableContext:";

    @Autowired
    private RedissonClient redissonClient;

    private Kryo5Codec kryo5Codec = new Kryo5Codec();

    public void storeExecuteContext(String executionUuid, ExecuteContext executeContext) {
        String redisKey = EXECUTE_CONTEXT + executionUuid;
        redissonClient.getBucket(redisKey, kryo5Codec).set(executeContext, Duration.of(1, ChronoUnit.HOURS));
    }

    public void storeVariableContext(String executionUuid, VariableContext variableContext) {
        String redisKey = VARIABLE_CONTEXT + executionUuid;
        redissonClient.getBucket(redisKey, kryo5Codec).set(variableContext, Duration.of(1, ChronoUnit.HOURS));
    }

    public ExecuteContext restoreExecuteContext(String executionUuid) {
        String redisKey = EXECUTE_CONTEXT + executionUuid;
        RBucket<ExecuteContext> bucket = redissonClient.getBucket(redisKey, kryo5Codec);
        return bucket.isExists() ? bucket.get() : null;
    }

    public VariableContext restoreVariableContext(String executionUuid) {
        String redisKey = VARIABLE_CONTEXT + executionUuid;
        RBucket<VariableContext> bucket = redissonClient.getBucket(redisKey, kryo5Codec);
        return bucket.isExists() ? bucket.get() : null;
    }

}
