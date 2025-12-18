package com.cmsr.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
    basePackages = "com.cmsr.v2.mapper",
    markerInterface = BaseMapper.class
)
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
public class MyBatisConfig {

}