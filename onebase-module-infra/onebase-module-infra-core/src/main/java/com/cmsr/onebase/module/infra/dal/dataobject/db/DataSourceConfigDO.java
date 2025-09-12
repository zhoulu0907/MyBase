package com.cmsr.onebase.module.infra.dal.dataobject.db;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据源配置
 *
 */
@Data
@SuperBuilder
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_data_source_config")
public class DataSourceConfigDO extends BaseDO {
    // builder模式可正常运作
    public DataSourceConfigDO setId(Long id){
        super.setId(id);
        return this;
    }
    /**
     * 主键编号 - Master 数据源
     */
    public static final Long ID_MASTER = 0L;

    // 字段常量定义
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    /**
     * 连接名
     */
    @Column(name = COLUMN_NAME)
    private String name;

    /**
     * 数据源连接
     */
    @Column(name = COLUMN_URL)
    private String url;
    /**
     * 用户名
     */
    @Column(name = COLUMN_USERNAME)
    private String username;
    /**
     * 密码
     */
    @Column(name = COLUMN_PASSWORD)
    private String password;

}