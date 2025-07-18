package com.cmsr.onebase.framework.common.util.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName Snow
 * @Description 雪花ID配置类，定义雪花ID生成器的参数
 * @Author mickey
 * @Date 2025/7/7 12:41
 */
@Slf4j
@Component
public class SnowflakeId {
    private static SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

    public static long nextId(){
        return idGenerator.nextId();
    }
}
