package com.cmsr.onebase.module.infra.dal.dataflexdo.file;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.framework.file.core.enums.FileStorageEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 文件配置表
 *
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_file_config")
public class FileConfigDO extends BaseTenantEntity {

    // 字段常量定义
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STORAGE = "storage";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_MASTER = "master";
    public static final String COLUMN_CONFIG = "config";

    /**
     * 配置名
     */
    @Column(value = COLUMN_NAME)
    private String name;
    /**
     * 存储器
     *
     * 枚举 {@link FileStorageEnum}
     */
    @Column(value = COLUMN_STORAGE)
    private Integer storage;
    /**
     * 备注
     */
    @Column(value = COLUMN_REMARK)
    private String remark;
    /**
     * 是否为主配置
     *
     * 由于我们可以配置多个文件配置，默认情况下，使用主配置进行文件的上传
     */
    @Column(value = COLUMN_MASTER)
    private Integer master;

    /**
     * 支付渠道配置
     */
    @Column(value = COLUMN_CONFIG)
    private Map<String, Object> config;


}