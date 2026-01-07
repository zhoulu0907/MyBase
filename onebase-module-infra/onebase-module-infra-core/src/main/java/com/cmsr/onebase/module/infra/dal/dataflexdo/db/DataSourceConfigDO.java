package com.cmsr.onebase.module.infra.dal.dataflexdo.db;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据源配置
 *
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_data_source_config")
public class DataSourceConfigDO extends BaseTenantEntity {
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
    @Column(value = COLUMN_NAME)
    private String name;

    /**
     * 数据源连接
     */
    @Column(value = COLUMN_URL)
    private String url;
    /**
     * 用户名
     */
    @Column(value = COLUMN_USERNAME)
    private String username;
    /**
     * 密码
     */
    @Column(value = COLUMN_PASSWORD)
    private String password;

}