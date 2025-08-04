package com.cmsr.onebase.module.infra.dal.dataobject.file;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.db.DBFileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.ftp.FtpFileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.s3.S3FileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.client.sftp.SftpFileClientConfig;
import com.cmsr.onebase.module.infra.framework.file.core.enums.FileStorageEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * 文件配置表
 *
 */
@Data
@Builder
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_file_config")
public class FileConfigDO extends BaseDO {
    // builder模式可正常运作
    public FileConfigDO setId(Long id){
        super.setId(id);
        return this;
    }
    
    // 字段常量定义
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STORAGE = "storage";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_MASTER = "master";
    public static final String COLUMN_CONFIG = "config";

    /**
     * 配置名
     */
    @Column(name = COLUMN_NAME)
    private String name;
    /**
     * 存储器
     *
     * 枚举 {@link FileStorageEnum}
     */
    @Column(name = COLUMN_STORAGE)
    private Integer storage;
    /**
     * 备注
     */
    @Column(name = COLUMN_REMARK)
    private String remark;
    /**
     * 是否为主配置
     *
     * 由于我们可以配置多个文件配置，默认情况下，使用主配置进行文件的上传
     */
    @Column(name = COLUMN_MASTER)
    private Boolean master;

    /**
     * 支付渠道配置
     */
    @Column(name = COLUMN_CONFIG)
    private FileClientConfig config;


}