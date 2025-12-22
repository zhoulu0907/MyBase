package com.cmsr.onebase.module.dashboard.build.config;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.spring.boot.MybatisFlexAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
    basePackages = "com.cmsr.v2.mapper",
    markerInterface = BaseMapper.class
)
@AutoConfigureBefore(MybatisFlexAutoConfiguration.class)
public class MyBatisConfig {

}