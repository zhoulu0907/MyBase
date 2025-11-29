package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程抄送记录表 实体类。
 *
 * @author liyang
 * @since 2025-11-28
 */
@Data
@Table("bpm_flow_cc_record")
public class BpmFlowCcRecordDO extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流程实例id
     */
    private Long instanceId;

    /**
     * 任务表id
     */
    private Long taskId;

    /**
     * 已阅 0，否 1，是
     */
    private Integer viewed;

    /**
     * 已读时间
     */
    private LocalDateTime viewedTime;

    /**
     * 抄送用户ID
     */
    private String userId;

    /**
     * 租户id
     */
    private Long tenantId;

}
