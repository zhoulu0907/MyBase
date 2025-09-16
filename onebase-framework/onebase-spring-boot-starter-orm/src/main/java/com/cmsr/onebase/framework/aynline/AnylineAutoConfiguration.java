package com.cmsr.onebase.framework.aynline;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * AnyLine配置类
 */
@AutoConfiguration
public class AnylineAutoConfiguration {


    /**
     * 初始化DataRepository
     */
    @Bean("dataDDLRepository")
    public DataDDLRepository createDataDDLRepository() {
        return new DataDDLRepository();
    }

}
