package com.cmsr.onebase.module.metadata.core.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 元数据模块配置类
 * <p>
 * 维护metadata模块所需的所有配置参数，包括默认数据源配置等
 *
 * @author matianyu
 * @date 2025-08-06
 */
@Component
@Getter
@Slf4j
public class MetadataConfig {

    // ========== 默认数据源配置 ==========

    /**
     * 默认数据源主机地址
     */
    @Value("${onebase.metadata.default-datasource.host:10.0.104.38}")
    private String defaultDatasourceHost;

    /**
     * 默认数据源端口号
     */
    @Value("${onebase.metadata.default-datasource.port:5432}")
    private Integer defaultDatasourcePort;

    /**
     * 默认数据库名称
     */
    @Value("${onebase.metadata.default-datasource.database:onebase_business}")
    private String defaultDatasourceDatabase;

    /**
     * 默认数据源用户名
     */
    @Value("${onebase.metadata.default-datasource.username:postgres}")
    private String defaultDatasourceUsername;

    /**
     * 默认数据源密码
     */
    @Value("${onebase.metadata.default-datasource.password:onebase@2025}")
    private String defaultDatasourcePassword;

    /**
     * 默认数据源类型
     */
    @Value("${onebase.metadata.default-datasource.type:PostgreSQL}")
    private String defaultDatasourceType;

    /**
     * 默认数据源描述
     */
    @Value("${onebase.metadata.default-datasource.description:默认数据源}")
    private String defaultDatasourceDescription;

    /**
     * 默认数据源运行模式：1-开发模式，2-测试模式，3-生产模式
     */
    @Value("${onebase.metadata.default-datasource.run-mode:1}")
    private Integer defaultDatasourceRunMode;

    /**
     * 默认数据源来源：0-系统默认，1-用户创建
     */
    @Value("${onebase.metadata.default-datasource.datasource-origin:0}")
    private Integer defaultDatasourceDatasourceOrigin;

    // ========== 数据方法配置 ==========

    /**
     * 是否启用系统内置数据方法
     */
    @Value("${onebase.metadata.data-method.enable-builtin-methods:true}")
    private Boolean dataMethodEnableBuiltinMethods;

    /**
     * 方法缓存过期时间（秒）
     */
    @Value("${onebase.metadata.data-method.method-cache-expiration:3600}")
    private Long dataMethodCacheExpiration;

    /**
     * 最大方法参数数量
     */
    @Value("${onebase.metadata.data-method.max-parameter-count:50}")
    private Integer dataMethodMaxParameterCount;

    /**
     * 方法调用超时时间（毫秒）
     */
    @Value("${onebase.metadata.data-method.method-timeout-ms:30000}")
    private Long dataMethodTimeoutMs;
    
    // ========== 数据源类型转换工具方法 ==========
    
    /**
     * 获取默认数据源类型对应的DatabaseType枚举
     * 如果配置的类型不是Anyline支持的枚举，返回null并记录警告
     *
     * @return DatabaseType枚举，如果不是有效枚举则返回null
     */
    public DatabaseType getDefaultDatasourceTypeEnum() {
        try {
            return DatabaseType.valueOf(defaultDatasourceType);
        } catch (IllegalArgumentException e) {
            log.warn("配置的默认数据源类型[{}]不是有效的DatabaseType枚举值，请检查配置", defaultDatasourceType);
            return null;
        }
    }
    
    /**
     * 验证数据源类型字符串是否是有效的DatabaseType枚举
     *
     * @param datasourceType 数据源类型字符串
     * @return true-有效，false-无效
     */
    public static boolean isValidDatabaseType(String datasourceType) {
        try {
            DatabaseType.valueOf(datasourceType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 将数据源类型字符串转换为DatabaseType枚举，如果失败返回默认值PostgreSQL
     *
     * @param datasourceType 数据源类型字符串
     * @return DatabaseType枚举
     */
    public static DatabaseType toDatabaseType(String datasourceType) {
        try {
            return DatabaseType.valueOf(datasourceType);
        } catch (IllegalArgumentException e) {
            log.warn("无法将字符串[{}]转换为DatabaseType枚举，使用默认值PostgreSQL", datasourceType);
            return DatabaseType.PostgreSQL;
        }
    }
}
