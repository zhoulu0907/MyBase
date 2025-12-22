package com.cmsr.onebase.module.infra.dal.dataflexdo.file;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件内容表
 *
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_file_content")
public class FileContentDO extends BaseTenantEntity {
    // builder模式可正常运作
    public FileContentDO setId(Long id){
        super.setId(id);
        return this;
    }

    // 字段常量定义
    public static final String COLUMN_CONFIG_ID = "config_id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SIZE = "size";

    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    @Column(value = COLUMN_CONFIG_ID)
    private Long configId;
    /**
     * 文件路径
     */
    @Column(value = COLUMN_PATH)
    private String path;
    /**
     * 文件内容
     */
    @Column(value = COLUMN_CONTENT)
    private byte[] content;
    /**
     * 文件大小
     */
    @Column(value = COLUMN_SIZE)
    private Long size;

}