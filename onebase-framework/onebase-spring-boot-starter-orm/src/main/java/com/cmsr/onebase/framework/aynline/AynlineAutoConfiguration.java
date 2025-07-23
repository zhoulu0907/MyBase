package com.cmsr.onebase.framework.aynline;

import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * AnyLine配置类
 */
@AutoConfiguration
@ConfigurationProperties(prefix = "spring.datasource")
public class AynlineAutoConfiguration {
//    @Value("${dynamic.datasource}")
    private String dsType;
    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    /**
     * 初始化anyline service
     */
    @Bean("anylineService")
    public AnylineService createAnylineService() {
        DataSource ds = DataSourceUtil.build(
                dsType, driverClassName, url, username, password
        );

        AnylineService<?> tempService = null;
        try {
            tempService = ServiceProxy.temporary(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempService;
    }

    /**
     * 初始化DataRepository
     */
    @Bean("dataRepository")
    public DataRepository createDataRepostory() {
        return new DataRepository();
    }


}
