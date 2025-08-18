package com.cmsr.onebase.framework.aynline;

import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.metadata.type.Convert;
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
public class AnylineAutoConfiguration {
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
            throw new RuntimeException(e);
        }
        return tempService;
    }

    /**
     * 初始化DataRepository
     */
    @Bean("dataDDLRepository")
    public DataDDLRepository createDataDDLRepository() {
        return new DataDDLRepository();
    }

    @Bean("convertInteger2Boolean")
    public Convert convertInteger2Boolean() {
        Convert convert = new Convert() {
            @Override
            public Class getOrigin() {
                return java.lang.Integer.class;
            }

            @Override
            public Class getTarget() {
                return java.lang.Boolean.class;
            }

            @Override
            public Object exe(Object value, Object def) {
                Integer date = (Integer) value;
                if (date == null) {
                    return null;
                } else if (date.intValue() > 0) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        };
        return convert;
    }

}
