package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Clob;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据采集-Catalog信息 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_catalog")
public class EtlCatalog extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源Id
     */
    private Long datasourceId;

    /**
     * catalog名称
     */
    private String catalogName;

    /**
     * 展示名称（用户可修改）
     */
    private String displayName;

    /**
     * 采集到的信息
     */
    private Clob metaInfo;

    /**
     * 采集到的描述
     */
    private String remarks;

    /**
     * 描述（用户可修改）
     */
    private String declaration;

}
