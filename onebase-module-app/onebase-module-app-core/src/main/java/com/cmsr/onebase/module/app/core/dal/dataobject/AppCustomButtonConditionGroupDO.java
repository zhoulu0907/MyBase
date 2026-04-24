package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_condition_group")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonConditionGroupDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("group_no")
    private Integer groupNo;

    @Column("logic_type")
    private String logicType;

    @Column("sort_no")
    private Integer sortNo;
}
