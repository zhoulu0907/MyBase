package com.cmsr.onebase.module.system.framework.test;


import com.cmsr.onebase.framework.redis.config.OneBaseCacheAutoConfiguration;
import com.cmsr.onebase.framework.redis.config.OneBaseRedisAutoConfiguration;
import com.cmsr.onebase.framework.test.config.SqlInitializationTestConfiguration;

import cn.hutool.extra.spring.SpringUtil;

import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 依赖数据库的集成测试基类
 * <p>
 * 提供数据库连接、事务回滚、Redis缓存等功能
 *
 * @author matianyu
 * @date 2025-08-06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbIntegrationTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
@Transactional // 自动回滚数据库事务，保证测试之间的隔离性
public class BaseDbIntegrationTest {

    @SpringBootApplication(
    scanBasePackages = "com.cmsr.onebase.module.system",
    exclude = {
        // 排除Redis自动配置
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        RedissonAutoConfigurationV2.class,
        // 排除自定义Redis配置
        OneBaseRedisAutoConfiguration.class,
        OneBaseCacheAutoConfiguration.class,
        // 排除缓存相关配置
        CacheAutoConfiguration.class,
        // 排除Web相关配置  
        WebMvcAutoConfiguration.class,
        // 排除安全配置
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        // 排除其他不需要的配置
        DataSourceAutoConfiguration.class // 我们使用自定义数据源配置
    }
)
    @Import({
            // 数据库配置类
            SqlInitializationTestConfiguration.class, // SQL 初始化配置
            
            // 测试专用监听器
            TestAnyLineDBInfoListener.class, // 自动添加软删除条件
            
            // 其它配置类
            SpringUtil.class
    })
    public static class Application {
    }

}
