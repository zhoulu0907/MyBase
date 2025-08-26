package com.cmsr.onebase.module.metadata.dal.dataobject.method;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 补偿日志 DO
 * 记录补偿操作与状态
 *
 * @author bty418
 * @date 2025-08-22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_compensation_log")
public class MetadataCompensationLogDO extends BaseDO {

    public static final String OUTBOX_ID = "outbox_id";
    public static final String COMPENSATION_ACTION = "compensation_action";
    public static final String PAYLOAD = "payload";
    public static final String STATUS = "status";
    public static final String ERROR_MSG = "error_msg";

    private Long outboxId;

    private String compensationAction;

    @Column(name = "payload")
    private String payload; // jsonb

    private String status;

    @Column(columnDefinition = "text")
    private String errorMsg;

}
