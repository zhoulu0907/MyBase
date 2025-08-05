package com.cmsr.onebase.module.infra.dal.dataobject.file;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件表
 * 每次文件上传，都会记录一条记录到该表中
 *
 */
@Data
@Builder
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_file")
public class FileDO extends BaseDO {

    // 字段常量定义
    public static final String COLUMN_CONFIG_ID = "config_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SIZE = "size";

    public FileDO setId(Long id){
        super.setId(id);
        return this;
    }

    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    @Column(name = COLUMN_CONFIG_ID)
    private Long configId;
    /**
     * 原文件名
     */
    @Column(name = COLUMN_NAME)
    private String name;
    /**
     * 路径，即文件名
     */
    @Column(name = COLUMN_PATH)
    private String path;
    /**
     * 访问地址
     */
    @Column(name = COLUMN_URL)
    private String url;
    /**
     * 文件的 MIME 类型，例如 "application/octet-stream"
     */
    @Column(name = COLUMN_TYPE)
    private String type;
    /**
     * 文件大小
     */
    @Column(name = COLUMN_SIZE)
    private Integer size;

}