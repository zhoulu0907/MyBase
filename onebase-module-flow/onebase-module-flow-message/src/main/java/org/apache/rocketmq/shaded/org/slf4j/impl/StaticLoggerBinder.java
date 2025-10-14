package org.apache.rocketmq.shaded.org.slf4j.impl;

import org.apache.rocketmq.shaded.org.slf4j.ILoggerFactory;
import org.apache.rocketmq.shaded.org.slf4j.helpers.NOPLoggerFactory;
import org.apache.rocketmq.shaded.org.slf4j.spi.LoggerFactoryBinder;

/**
 * @Author：huangjie
 * @Date：2025/10/14 12:19
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private static final String loggerFactoryClassStr = NOPLoggerFactory.class.getName();
    private final ILoggerFactory loggerFactory = new NOPLoggerFactory();

    public static final StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    private StaticLoggerBinder() {
    }

    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    public String getLoggerFactoryClassStr() {
        return loggerFactoryClassStr;
    }
}
