package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Table("app_custom_button_exec_log")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonExecLogDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("button_code")
    private String buttonCode;

    @Column("button_name")
    private String buttonName;

    @Column("action_type")
    private String actionType;

    @Column("operator_user_id")
    private Long operatorUserId;

    @Column("operator_dept_id")
    private Long operatorDeptId;

    @Column("menu_uuid")
    private String menuUuid;

    @Column("pageset_uuid")
    private String pageSetUuid;

    @Column("page_uuid")
    private String pageUuid;

    @Column("record_id")
    private String recordId;

    @Column("batch_no")
    private String batchNo;

    @Column("operation_scope")
    private String operationScope;

    @Column("exec_status")
    private String execStatus;

    @Column("error_code")
    private String errorCode;

    @Column("error_message")
    private String errorMessage;

    @Column("request_snapshot")
    private String requestSnapshot;

    @Column("response_snapshot")
    private String responseSnapshot;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("end_time")
    private LocalDateTime endTime;

    @Column("duration_ms")
    private Long durationMs;
}
