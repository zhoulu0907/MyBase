package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  实体类。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_schema")
public class EtlSchema extends TenantBaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    private Long id;

    private Long applicationId;

    /**
     * 数据源Id
     */
    private Long datasourceId;

    /**
     * catalog表Id
     */
    private Long catalogId;

    /**
     * 名称
     */
    private String schemaName;

    /**
     * 展示名称（用户可修改）
     */
    private String displayName;

    /**
     * 采集的数据
     */
    private String metaInfo;

    /**
     * 采集到的描述
     */
    private String remarks;

    /**
     * 描述（用户可修改）
     */
    private String declaration;

    /**
     * 是否删除（逻辑删除）
     */
    private Long deleted;

    private Long creator;

    private Timestamp createTime;

    private Long updater;

    private Timestamp updateTime;

    private Integer lockVersion;

}
