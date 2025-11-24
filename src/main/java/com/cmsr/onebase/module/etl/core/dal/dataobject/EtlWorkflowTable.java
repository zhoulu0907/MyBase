package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ETL工作流与表关联关系表 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_workflow_table")
public class EtlWorkflowTable extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long workflowId;

    private String relation;

    private Long tableId;

    private Long datasourceId;

}
