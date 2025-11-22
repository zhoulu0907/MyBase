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
@Table("etl_workflow")
public class EtlWorkflow extends TenantBaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    private Long id;

    /**
     * 应用Id
     */
    private Long applicationId;

    /**
     * 名称
     */
    private String workflowName;

    /**
     * 配置信息
     */
    private String config;

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
    private String scheduleConfig;

    /**
     * 是否删除（逻辑删除）
     */
    private Long deleted;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 乐观锁版本
     */
    private Integer lockVersion;

    /**
     * ETL描述
     */
    private String declaration;

}
