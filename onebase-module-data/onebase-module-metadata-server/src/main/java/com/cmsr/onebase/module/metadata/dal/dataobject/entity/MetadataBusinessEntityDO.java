package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import jakarta.persistence.Table;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 业务实体表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(name = "metadata_business_entity")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataBusinessEntityDO extends TenantBaseDO {

    // 列名常量
    public static final String DISPLAY_NAME   = "display_name";
    public static final String CODE           = "code";
    public static final String ENTITY_TYPE    = "entity_type";
    public static final String DESCRIPTION    = "description";
    public static final String DATASOURCE_ID  = "datasource_id";
    public static final String TABLE_NAME     = "table_name";
    public static final String RUN_MODE       = "run_mode";
    public static final String APP_ID         = "app_id";
    public static final String DISPLAY_CONFIG = "display_config";

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
     * 实体类型(1:自建表 2:复用已有表 3中间表用做多对多关联用不给前端展示)
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
