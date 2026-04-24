package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_condition_item")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonConditionItemDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("group_id")
    private Long groupId;

    @Column("field_uuid")
    private String fieldUuid;

    @Column("field_code")
    private String fieldCode;

    @Column("operator")
    private String operator;

    @Column("value_type")
    private String valueType;

    @Column("compare_value")
    private String compareValue;

    @Column("sort_no")
    private Integer sortNo;
}
