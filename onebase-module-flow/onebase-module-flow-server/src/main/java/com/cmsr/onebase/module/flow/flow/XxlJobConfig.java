package com.cmsr.onebase.module.flow.flow;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * xxl-flow config
 *
 * @author xuxueli 2017-04-28
 */
//@Configuration
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${flow.job.admin.addresses}")
    private String adminAddresses;

    @Value("${flow.job.admin.accessToken}")
    private String accessToken;

    @Value("${flow.job.admin.timeout}")
    private int timeout;

    @Value("${flow.job.executor.appname}")
    private String appname;

    @Value("${flow.job.executor.address:}")
    private String address;

    @Value("${flow.job.executor.ip:}")
    private String ip;

    @Value("${flow.job.executor.port:0}")
    private int port;

    @Value("${flow.job.executor.logpath:}")
    private String logPath;

    @Value("${flow.job.executor.logretentiondays:3}")
    private int logRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info("xxl-flow config: [{}] [{}]", adminAddresses, appname);
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setTimeout(timeout);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }

}