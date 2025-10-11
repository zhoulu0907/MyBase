package com.cmsr.onebase.module.flow.context;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/11 21:33
 */
@Component
public class ContextProvider implements InitializingBean {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(redisConnectionFactory);
        this.redisTemplate.afterPropertiesSet();
    }

    // 序列化
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    // 反序列化
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public void storeExecuteContext(String executionUuid, ExecuteContext executeContext) throws IOException {
        byte[] serialize = serialize(executeContext);
        redisTemplate.opsForValue().set("executeContext:" + executionUuid, serialize, 1, TimeUnit.HOURS);

    }

    public void storeVariableContext(String executionUuid, VariableContext variableContext) throws IOException {
        byte[] serialize = serialize(variableContext);
        redisTemplate.opsForValue().set("variableContext:" + executionUuid, serialize, 1, TimeUnit.HOURS);
    }

    public ExecuteContext restoreExecuteContext(String executionUuid) throws IOException, ClassNotFoundException {
        byte[] bytes = (byte[]) redisTemplate.opsForValue().get("executeContext:" + executionUuid);
        ExecuteContext deserialize = (ExecuteContext) deserialize(bytes);
        return deserialize;
    }

    public VariableContext restoreVariableContext(String executionUuid) throws IOException, ClassNotFoundException {
        byte[] bytes = (byte[]) redisTemplate.opsForValue().get("variableContext:" + executionUuid);
        VariableContext deserialize = (VariableContext) deserialize(bytes);
        return deserialize;
    }


}
