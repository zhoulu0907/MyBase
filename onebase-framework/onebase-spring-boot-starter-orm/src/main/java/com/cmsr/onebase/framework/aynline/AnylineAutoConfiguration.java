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
 * 
 * 解决达梦数据库等特殊数据库的类型转换问题
 * 通过ConvertProxy注册类型转换器,支持DmdbTimestamp/DmdbDate/DmdbTime等特殊JDBC类型
 * 到LocalDateTime/LocalDate/LocalTime的自动转换
 *
 * @author matianyu
 * @date 2025-11-11
 */
@Slf4j
@AutoConfiguration
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnylineAutoConfiguration {

    /**
     * 尝试注册DM数据库特定的JDBC类型转换器
     * 如果DM驱动类不存在,则跳过
     */
    private static void registerDmdbConverters() {
        try {
            // 尝试加载DM数据库的Timestamp类
            Class<?> dmdbTimestampClass = Class.forName("dm.jdbc.driver.DmdbTimestamp");
            System.out.println("[AnylineAutoConfiguration] 检测到DM数据库驱动,注册DmdbTimestamp转换器");
            
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
                    if (value instanceof Timestamp) {
                        LocalDateTime result = ((Timestamp) value).toLocalDateTime();
                        System.out.println("[ConvertProxy-Dmdb] DmdbTimestamp转LocalDateTime: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                }
            });
            System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: DmdbTimestamp -> LocalDateTime");
            
            // 注册DmdbDate
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
                    if (value instanceof Date) {
                        LocalDate result = ((Date) value).toLocalDate();
                        System.out.println("[ConvertProxy-Dmdb] DmdbDate转LocalDate: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                }
            });
            System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: DmdbDate -> LocalDate");
            
            // 注册DmdbTime
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
                    if (value instanceof Time) {
                        LocalTime result = ((Time) value).toLocalTime();
                        System.out.println("[ConvertProxy-Dmdb] DmdbTime转LocalTime: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                }
            });
            System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: DmdbTime -> LocalTime");
            
        } catch (ClassNotFoundException e) {
            System.out.println("[AnylineAutoConfiguration] 未检测到DM数据库驱动,跳过DM特定类型注册");
        }
    }

    /**
     * 静态初始化块：在类加载时注册ConvertProxy类型转换器
     * BeanUtil.setFieldValue()会调用ConvertProxy.getConvert()查找转换器
     * 必须在static块中注册,确保在Spring容器初始化前完成注册
     */
    static {
        System.out.println("[AnylineAutoConfiguration] 开始注册Anyline ConvertProxy类型转换器");
        
        // 先尝试注册DM数据库的特定类型（如果类存在的话）
        registerDmdbConverters();
        
        // 注册 Timestamp -> LocalDateTime 转换器
        // DmdbTimestamp继承自java.sql.Timestamp,通过注册Timestamp转换器来支持DmdbTimestamp
        ConvertProxy.reg(new Convert() {
            @Override
            public Class getOrigin() {
                return Timestamp.class;  // 注册Timestamp而不是java.util.Date
            }

            @Override
            public Class getTarget() {
                return LocalDateTime.class;
            }

            @Override
            public Object exe(Object value, Object def) throws ConvertException {
                System.out.println("[ConvertProxy] =====> exe()方法被调用! value=" + value + ", valueType=" + (value != null ? value.getClass().getName() : "null"));
                try {
                    if (value == null) {
                        return def;
                    }
                    // 处理Timestamp及其子类（如DmdbTimestamp）
                    if (value instanceof Timestamp) {
                        // 如果是Timestamp的子类(如DmdbTimestamp),动态注册该子类的转换器
                        Class<?> actualClass = value.getClass();
                        if (actualClass != Timestamp.class && ConvertProxy.getConvert(actualClass, LocalDateTime.class) == null) {
                            System.out.println("[ConvertProxy] 检测到Timestamp子类: " + actualClass.getName() + ",动态注册转换器");
                            Convert thisConvert = this;
                            ConvertProxy.reg(new Convert() {
                                @Override
                                public Class getOrigin() {
                                    return actualClass;
                                }
                                @Override
                                public Class getTarget() {
                                    return LocalDateTime.class;
                                }
                                @Override
                                public Object exe(Object v, Object d) throws ConvertException {
                                    return thisConvert.exe(v, d);  // 复用当前转换逻辑
                                }
                            });
                        }
                        
                        LocalDateTime result = ((Timestamp) value).toLocalDateTime();
                        System.out.println("[ConvertProxy] " + value.getClass().getSimpleName() + "转LocalDateTime成功: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                } catch (Exception e) {
                    String typeName = (value != null) ? value.getClass().getName() : "null";
                    System.err.println("[ConvertProxy] Timestamp类型转LocalDateTime失败: value=" + value + ", type=" + typeName + ", error=" + e.getMessage());
                    e.printStackTrace();
                    ConvertException ce = new ConvertException();
                    ce.initCause(e);
                    throw ce;
                }
            }
        });
        System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: Timestamp -> LocalDateTime");
        
        // 注册 Date -> LocalDate 转换器
        // DmdbDate继承自java.sql.Date,通过注册Date转换器来支持DmdbDate
        ConvertProxy.reg(new Convert() {
            @Override
            public Class getOrigin() {
                return Date.class;  // 注册java.sql.Date而不是java.util.Date
            }

            @Override
            public Class getTarget() {
                return LocalDate.class;
            }

            @Override
            public Object exe(Object value, Object def) throws ConvertException {
                try {
                    if (value == null) {
                        return def;
                    }
                    // 处理SQL Date及其子类（如DmdbDate）
                    if (value instanceof Date) {
                        // 如果是Date的子类(如DmdbDate),动态注册该子类的转换器
                        Class<?> actualClass = value.getClass();
                        if (actualClass != Date.class && ConvertProxy.getConvert(actualClass, LocalDate.class) == null) {
                            System.out.println("[ConvertProxy] 检测到Date子类: " + actualClass.getName() + ",动态注册转换器");
                            Convert thisConvert = this;
                            ConvertProxy.reg(new Convert() {
                                @Override
                                public Class getOrigin() {
                                    return actualClass;
                                }
                                @Override
                                public Class getTarget() {
                                    return LocalDate.class;
                                }
                                @Override
                                public Object exe(Object v, Object d) throws ConvertException {
                                    return thisConvert.exe(v, d);
                                }
                            });
                        }
                        
                        LocalDate result = ((Date) value).toLocalDate();
                        System.out.println("[ConvertProxy] " + value.getClass().getSimpleName() + "转LocalDate成功: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                } catch (Exception e) {
                    String typeName = (value != null) ? value.getClass().getName() : "null";
                    System.err.println("[ConvertProxy] Date类型转LocalDate失败: value=" + value + ", type=" + typeName + ", error=" + e.getMessage());
                    e.printStackTrace();
                    ConvertException ce = new ConvertException();
                    ce.initCause(e);
                    throw ce;
                }
            }
        });
        System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: Date -> LocalDate");
        
        // 注册 Time -> LocalTime 转换器
        // DmdbTime继承自java.sql.Time,通过注册Time转换器来支持DmdbTime
        ConvertProxy.reg(new Convert() {
            @Override
            public Class getOrigin() {
                return Time.class;  // 注册java.sql.Time而不是java.util.Date
            }

            @Override
            public Class getTarget() {
                return LocalTime.class;
            }

            @Override
            public Object exe(Object value, Object def) throws ConvertException {
                try {
                    if (value == null) {
                        return def;
                    }
                    // 处理SQL Time及其子类（如DmdbTime）
                    if (value instanceof Time) {
                        // 如果是Time的子类(如DmdbTime),动态注册该子类的转换器
                        Class<?> actualClass = value.getClass();
                        if (actualClass != Time.class && ConvertProxy.getConvert(actualClass, LocalTime.class) == null) {
                            System.out.println("[ConvertProxy] 检测到Time子类: " + actualClass.getName() + ",动态注册转换器");
                            Convert thisConvert = this;
                            ConvertProxy.reg(new Convert() {
                                @Override
                                public Class getOrigin() {
                                    return actualClass;
                                }
                                @Override
                                public Class getTarget() {
                                    return LocalTime.class;
                                }
                                @Override
                                public Object exe(Object v, Object d) throws ConvertException {
                                    return thisConvert.exe(v, d);
                                }
                            });
                        }
                        
                        LocalTime result = ((Time) value).toLocalTime();
                        System.out.println("[ConvertProxy] " + value.getClass().getSimpleName() + "转LocalTime成功: " + value + " -> " + result);
                        return result;
                    }
                    return def;
                } catch (Exception e) {
                    String typeName = (value != null) ? value.getClass().getName() : "null";
                    System.err.println("[ConvertProxy] Time类型转LocalTime失败: value=" + value + ", type=" + typeName + ", error=" + e.getMessage());
                    e.printStackTrace();
                    ConvertException ce = new ConvertException();
                    ce.initCause(e);
                    throw ce;
                }
            }
        });
        System.out.println("[AnylineAutoConfiguration] 已注册ConvertProxy: Time -> LocalTime");
        
        System.out.println("[AnylineAutoConfiguration] Anyline ConvertProxy类型转换器注册完成");
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
