package com.cmsr.onebase.module.bpm.core.dal.dataobject;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程抄送记录表
 *
 */
@Data
@Table(name = "bpm_flow_cc_record")
public class BpmFlowCcRecordDO extends TenantBaseDO {

    public static final String INSTANCE_ID = "instance_id";

    public static final String TASK_ID = "task_id";

    public static final String VIEWED = "viewed";

    public static final String VIEWED_TIME = "viewed_time";

    public static final String USER_ID = "user_id";


    /**
     * 流程实例id
     */
    @Column(name = "instance_id")
    private Long instanceId;

    /**
     * 任务表id
     */
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 已阅 0，否 1，是
     */
    @Column(name = "viewed")
    private Integer viewed;

    /**
     * 已读时间
     */
    @Column(name = "viewed_time")
    private LocalDateTime viewedTime;

    /**
     * 抄送用户ID
     */
    @Column(name = "user_id", length = 80)
    private String userId;


}
