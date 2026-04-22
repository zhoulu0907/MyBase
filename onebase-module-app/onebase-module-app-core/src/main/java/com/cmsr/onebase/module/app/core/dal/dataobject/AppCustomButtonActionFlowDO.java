package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_action_flow")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonActionFlowDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("flow_process_id")
    private Long flowProcessId;

    @Column("flow_process_uuid")
    private String flowProcessUuid;

    @Column("confirm_required")
    private Integer confirmRequired;

    @Column("confirm_text")
    private String confirmText;
}
