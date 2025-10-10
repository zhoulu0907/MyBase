package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WarmFlow 流程用户 DO，对应表 flow_user。
 *
 * @author liyang
 * @date 2025-09-29
 */
@Data
@Accessors(chain = true)
@Table(name = "flow_user")
public class BpmFlowUserDO extends BpmWarmFlowBaseDO {

    /** 人员类型（1审批人 2转办人 3委托人） */
    @Column(name = "type", length = 1, nullable = false)
    private String type;

    /** 权限人（可能是角色ID或用户ID等） */
    @Column(name = "processed_by", length = 80)
    private String processedBy;

    /** 关联的任务ID */
    @Column(name = "associated", nullable = false)
    private Long associated;
}


