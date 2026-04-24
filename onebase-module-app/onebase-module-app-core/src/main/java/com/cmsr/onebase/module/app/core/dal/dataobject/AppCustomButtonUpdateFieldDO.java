package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_update_field")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonUpdateFieldDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("field_mode")
    private String fieldMode;

    @Column("field_uuid")
    private String fieldUuid;

    @Column("field_code")
    private String fieldCode;

    @Column("required_flag")
    private Integer requiredFlag;

    @Column("value_type")
    private String valueType;

    @Column("value_config")
    private String valueConfig;

    @Column("sort_no")
    private Integer sortNo;
}
