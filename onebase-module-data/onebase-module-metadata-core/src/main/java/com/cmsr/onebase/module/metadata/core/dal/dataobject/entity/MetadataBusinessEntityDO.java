package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务实体表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_business_entity")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataBusinessEntityDO extends BaseTenantEntity {

    /**
     * 实体名称
     */
    @Column(value = "display_name", comment = "实体名称")
    private String displayName;

    /**
     * 实体编码
     */
    @Column(value = "code", comment = "实体编码")
    private String code;

    /**
     * 实体类型(1:自建表 2:复用已有表 3中间表用做多对多关联用不给前端展示)
     */
    @Column(value = "entity_type", comment = "实体类型")
    private Integer entityType;

    /**
     * 实体描述
     */
    @Column(value = "description", comment = "实体描述")
    private String description;

    /**
     * 数据源ID
     */
    @Column(value = "datasource_id", comment = "数据源ID")
    private Long datasourceId;

    /**
     * 对应数据表名
     */
    @Column(value = "table_name", comment = "对应数据表名")
    private String tableName;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    @Column(value = "run_mode", comment = "运行模式：0 编辑态，1 运行态")
    private Integer runMode;

    /**
     * 应用ID
     */
    @Column(value = "app_id", comment = "应用ID")
    private Long appId;

    /**
     * 前端显示配置json
     */
    @Column(value = "display_config", comment = "前端显示配置json")
    private String displayConfig;

    /**
     * 状态：0 关闭，1 开启
     */
    @Column(value = "status", comment = "状态：0 关闭，1 开启")
    private Integer status;

}
