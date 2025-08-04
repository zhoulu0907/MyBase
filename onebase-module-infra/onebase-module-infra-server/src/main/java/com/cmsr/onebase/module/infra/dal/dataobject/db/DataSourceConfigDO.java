package com.cmsr.onebase.module.infra.dal.dataobject.db;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.mybatis.core.type.EncryptTypeHandler;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据源配置
 *
 */
@Data
@Builder
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
    @TableField(value = COLUMN_NAME)
    private String name;

    /**
     * 数据源连接
     */
    @TableField(value = COLUMN_URL)
    private String url;
    /**
     * 用户名
     */
    @TableField(value = COLUMN_USERNAME)
    private String username;
    /**
     * 密码
     */
    @TableField(value = COLUMN_PASSWORD, typeHandler = EncryptTypeHandler.class)
    private String password;

}