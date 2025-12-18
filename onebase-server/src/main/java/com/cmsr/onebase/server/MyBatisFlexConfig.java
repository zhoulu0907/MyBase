package com.cmsr.onebase.server;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.cmsr.onebase.**.mapper")
// @MapperScan(basePackages = "com.cmsr.onebase.**.mapper", annotationClass = BaseMapper.class)
public class MyBatisFlexConfig implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig config) {
        // 自定义配置
    }
}