package com.cmsr.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.cmsr.v2.mapper"})
public class MyBatisConfig {

} 