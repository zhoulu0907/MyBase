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
 *  实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_workflow")
public class EtlWorkflow extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String workflowName;

    /**
     * 配置信息
     */
    private Clob config;

    /**
     * 启用状态,默认为关闭(0)
     */
    private Integer isEnabled;

    /**
     * 调度策略
     */
    private String scheduleStrategy;

    /**
     * 调度配置
     */
    private Clob scheduleConfig;

    /**
     * ETL描述
     */
    private String declaration;

}
