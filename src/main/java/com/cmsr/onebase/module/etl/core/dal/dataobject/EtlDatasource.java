package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ETL数据源配置 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_datasource")
public class EtlDatasource extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源编码
     */
    private String datasourceCode;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型，比如MySQL、PostgreSQL等
     */
    private String datasourceType;

    /**
     * 数据源配置信息（JSON）
     */
    private Clob config;

    /**
     * 采集状态，枚举值(none,required,success,failed,running)，默认为none
     */
    private String collectStatus;

    /**
     * 采集开始时间
     */
    private Timestamp collectStartTime;

    /**
     * 采集结束时间
     */
    private Timestamp collectEndTime;

    /**
     * 是否只读数据源
     */
    private Integer readonly;

    /**
     * 数据源描述
     */
    private String declaration;

}
