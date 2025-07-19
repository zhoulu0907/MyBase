package com.cmsr.onebase.framework.common.anyline.web;

import lombok.Data;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @ClassName MyAnyLineService
 * @Description 数据源配置类，提供数据库连接和AnyLine服务配置
 * @Author mickey
 * @Date 2025/6/27 22:11
 */
@Data
//@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class MyAnyLineService {
    private final AnylineService<?> service;

    @Value("${spring.datasource.type}")
    private String dsType;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    private MyAnyLineService() {
        DataSource ds = DataSourceUtil.build(
                dsType, driverClassName, url, username, password
        );

        AnylineService<?> tempService = null;
        try {
            tempService = ServiceProxy.temporary(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.service = tempService;
    }

    private static class SingletonHolder {
        private static final MyAnyLineService INSTANCE = new MyAnyLineService();
    }

    public static MyAnyLineService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public AnylineService<?> getService() {
        return service;
    }

}
