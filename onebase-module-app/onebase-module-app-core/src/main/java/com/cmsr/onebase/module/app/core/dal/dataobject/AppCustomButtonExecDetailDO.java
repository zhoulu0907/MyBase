package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_exec_detail")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonExecDetailDO extends BaseBizEntity {

    @Column("exec_log_id")
    private Long execLogId;

    @Column("batch_no")
    private String batchNo;

    @Column("record_id")
    private String recordId;

    @Column("exec_status")
    private String execStatus;

    @Column("error_code")
    private String errorCode;

    @Column("error_message")
    private String errorMessage;

    @Column("result_snapshot")
    private String resultSnapshot;
}
