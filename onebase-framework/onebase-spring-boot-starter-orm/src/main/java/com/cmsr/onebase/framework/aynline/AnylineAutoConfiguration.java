package com.cmsr.onebase.framework.aynline;

import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.Convert;
import org.anyline.metadata.type.ConvertException;
import org.anyline.proxy.ConvertProxy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * AnyLine配置类
 * <p>
 * 针对达梦数据库的特殊JDBC类型转换问题，通过ConvertProxy注册专用转换器
 * 支持 DmdbTimestamp/DmdbDate/DmdbTime 到 LocalDateTime/LocalDate/LocalTime 的自动转换
 * <p>
 * 注意：
 * 1. 仅在检测到达梦数据库驱动时才注册转换器
 * 2. PostgreSQL、MySQL等标准数据库使用Anyline原生转换机制，无需额外注册
 * 3. 避免注册通用类型（如Timestamp、Date）的转换器，会影响所有数据库的性能
 *
 * @author matianyu
 * @date 2025-11-11
 */
@Slf4j
@AutoConfiguration
@SuppressWarnings("rawtypes")
public class AnylineAutoConfiguration {

    /**
     * 注册达梦数据库特定的JDBC类型转换器
     * <p>
     * 只有当达梦数据库驱动类存在时才注册转换器
     * 其他数据库（如PostgreSQL、MySQL等）使用Anyline原生转换机制
     */
    private static void registerDmdbConverters() {
        try {
            // 尝试加载DM数据库的Timestamp类
            Class<?> dmdbTimestampClass = Class.forName("dm.jdbc.driver.DmdbTimestamp");
            log.info("[AnylineAutoConfiguration] 检测到达梦数据库驱动，开始注册DM特定类型转换器");
            
            // 注册 DmdbTimestamp -> LocalDateTime 转换器
            ConvertProxy.reg(new Convert() {
                @Override
                public Class getOrigin() {
                    return dmdbTimestampClass;
                }
                @Override
                public Class getTarget() {
                    return LocalDateTime.class;
                }
                @Override
                public Object exe(Object value, Object def) throws ConvertException {
                    if (value == null) {
                        return def;
                    }
                    try {
                        if (value instanceof Timestamp) {
                            return ((Timestamp) value).toLocalDateTime();
                        }
                    } catch (Exception e) {
                        log.error("[ConvertProxy] DmdbTimestamp转LocalDateTime失败: {}", e.getMessage());
                        ConvertException ce = new ConvertException();
                        ce.initCause(e);
                        throw ce;
                    }
                    return def;
                }
            });
            log.info("[AnylineAutoConfiguration] 已注册: DmdbTimestamp -> LocalDateTime");
            
            // 注册 DmdbDate -> LocalDate 转换器
            Class<?> dmdbDateClass = Class.forName("dm.jdbc.driver.DmdbDate");
            ConvertProxy.reg(new Convert() {
                @Override
                public Class getOrigin() {
                    return dmdbDateClass;
                }
                @Override
                public Class getTarget() {
                    return LocalDate.class;
                }
                @Override
                public Object exe(Object value, Object def) throws ConvertException {
                    if (value == null) {
                        return def;
                    }
                    try {
                        if (value instanceof Date) {
                            return ((Date) value).toLocalDate();
                        }
                    } catch (Exception e) {
                        log.error("[ConvertProxy] DmdbDate转LocalDate失败: {}", e.getMessage());
                        ConvertException ce = new ConvertException();
                        ce.initCause(e);
                        throw ce;
                    }
                    return def;
                }
            });
            log.info("[AnylineAutoConfiguration] 已注册: DmdbDate -> LocalDate");
            
            // 注册 DmdbTime -> LocalTime 转换器
            Class<?> dmdbTimeClass = Class.forName("dm.jdbc.driver.DmdbTime");
            ConvertProxy.reg(new Convert() {
                @Override
                public Class getOrigin() {
                    return dmdbTimeClass;
                }
                @Override
                public Class getTarget() {
                    return LocalTime.class;
                }
                @Override
                public Object exe(Object value, Object def) throws ConvertException {
                    if (value == null) {
                        return def;
                    }
                    try {
                        if (value instanceof Time) {
                            return ((Time) value).toLocalTime();
                        }
                    } catch (Exception e) {
                        log.error("[ConvertProxy] DmdbTime转LocalTime失败: {}", e.getMessage());
                        ConvertException ce = new ConvertException();
                        ce.initCause(e);
                        throw ce;
                    }
                    return def;
                }
            });
            log.info("[AnylineAutoConfiguration] 已注册: DmdbTime -> LocalTime");
            
        } catch (ClassNotFoundException e) {
            log.debug("[AnylineAutoConfiguration] 未检测到达梦数据库驱动，跳过DM特定类型注册");
        }
    }

    /**
     * 静态初始化块：在类加载时注册ConvertProxy类型转换器
     * <p>
     * 只注册达梦数据库特定的类型转换器（DmdbTimestamp/DmdbDate/DmdbTime）
     * PostgreSQL等标准数据库使用Anyline原生的类型转换机制，无需额外注册
     * <p>
     * 注意：不要注册通用的 Timestamp/Date/Time 转换器，否则会影响所有数据库的性能
     */
    static {
        System.out.println("[AnylineAutoConfiguration] 开始注册Anyline ConvertProxy类型转换器");
        
        // 只注册DM数据库的特定类型（如果类存在的话）
        // PostgreSQL等标准数据库不需要注册转换器，使用Anyline默认机制即可
        registerDmdbConverters();
        
        System.out.println("[AnylineAutoConfiguration] Anyline ConvertProxy类型转换器注册完成");
        System.out.println("[AnylineAutoConfiguration] 说明: 仅注册了达梦数据库特定类型，PostgreSQL等标准数据库使用Anyline原生转换");
    }

    /**
     * 初始化DataDDLRepository
     * 用于动态创建和管理数据库表结构
     *
     * @return DataDDLRepository实例
     */
    @Bean("dataDDLRepository")
    public DataDDLRepository createDataDDLRepository() {
        return new DataDDLRepository();
    }

}
