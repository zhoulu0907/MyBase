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
 * ETL采集的表信息 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_table")
public class EtlTable extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源Id
     */
    private Long datasourceId;

    /**
     * catalog表Id
     */
    private Long catalogId;

    /**
     * schema表id
     */
    private Long schemaId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表类别，如table和view等
     */
    private String tableType;

    /**
     * 表展示名称（用户可修改）
     */
    private String displayName;

    /**
     * 采集表信息-字段信息
     */
    private Clob metaInfo;

    /**
     * 采集到的表描述
     */
    private String remarks;

    /**
     * 表的描述（用户可修改）
     */
    private String declaration;

}
