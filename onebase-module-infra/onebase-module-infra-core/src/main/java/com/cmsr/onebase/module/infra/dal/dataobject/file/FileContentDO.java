package com.cmsr.onebase.module.infra.dal.dataobject.file;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件内容表
 *
 */
@Data
@SuperBuilder
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_file_content")
public class FileContentDO extends BaseDO {
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
    @Column(name = COLUMN_CONFIG_ID)
    private Long configId;
    /**
     * 文件路径
     */
    @Column(name = COLUMN_PATH)
    private String path;
    /**
     * 文件内容
     */
    @Column(name = COLUMN_CONTENT)
    private byte[] content;
    /**
     * 文件大小
     */
    @Column(name = COLUMN_SIZE)
    private Long size;

}