package com.cmsr.onebase.module.metadata.dal.dataobject.datasource;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.util.Map;

/**
 * 数据源表 DO
 */
@TableName(value = "metadata_datasource", autoResultMap = true)
@KeySequence("metadata_datasource_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDatasourceDO extends TenantBaseDO {

    public MetadataDatasourceDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源编码
     */
    private String code;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    private String datasourceType;

    /**
     * 数据源配置信息(JSON格式存储所有连接参数)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 描述
     */
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 版本锁标识
     */
    private Integer lockVersion;

}
