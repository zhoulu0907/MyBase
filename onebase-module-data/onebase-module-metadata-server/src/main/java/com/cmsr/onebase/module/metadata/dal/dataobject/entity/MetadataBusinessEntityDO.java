package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 业务实体表 DO
 */
@TableName(value = "metadata_business_entity")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataBusinessEntityDO extends TenantBaseDO {

    public MetadataBusinessEntityDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 实体名称
     */
    private String displayName;

    /**
     * 实体编码
     */
    private String code;

    /**
     * 实体类型(1:实体业务模型 2:虚拟业务模型)
     */
    private Integer entityType;

    /**
     * 实体描述
     */
    private String description;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 对应数据表名
     */
    private String tableName;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 前端显示配置json
     */
    private String displayConfig;


}
