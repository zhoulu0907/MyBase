package com.cmsr.onebase.module.infra.dal.dataflexdo.file;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件表
 * 每次文件上传，都会记录一条记录到该表中
 *
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_file")
public class FileDO extends BaseEntity {

    // 字段常量定义
    public static final String COLUMN_CONFIG_ID = "config_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_MD5 = "md5";
    public static final String COLUMN_VISIT_MODE = "visit_mode";
    public static final String COLUMN_RUN_MODE = "run_mode";

    public FileDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    @Column(value = COLUMN_CONFIG_ID)
    private Long configId;
    /**
     * 原文件名
     */
    @Column(value = COLUMN_NAME)
    private String name;
    /**
     * 路径，即文件名
     */
    @Column(value = COLUMN_PATH)
    private String path;
    /**
     * 访问地址
     */
    @Column(value = COLUMN_URL)
    private String url;
    /**
     * 文件的 MIME 类型，例如 "application/octet-stream"
     */
    @Column(value = COLUMN_TYPE)
    private String type;
    /**
     * 文件大小
     */
    @Column(value = COLUMN_SIZE)
    private Integer size;

    /**
     * 文件 MD5值
     */
    @Column(value = COLUMN_MD5)
    private String md5;

    /**
     * 文件 权限标识
     */
    @Column(value = COLUMN_VISIT_MODE)
    private String visitMode;

    /**
     * 文件 环境标识
     */
    @Column(value = COLUMN_RUN_MODE)
    private String runMode;

}