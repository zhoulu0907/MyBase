package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("menu_uuid")
    private String menuUuid;

    @Column("pageset_uuid")
    private String pageSetUuid;

    @Column("page_uuid")
    private String pageUuid;

    @Column("button_code")
    private String buttonCode;

    @Column("button_name")
    private String buttonName;

    @Column("button_desc")
    private String buttonDesc;

    @Column("show_desc")
    private Integer showDesc;

    @Column("style_type")
    private String styleType;

    @Column("color_hex")
    private String colorHex;

    @Column("color_alpha")
    private Integer colorAlpha;

    @Column("icon_code")
    private String iconCode;

    @Column("operation_scope")
    private String operationScope;

    @Column("show_in_form")
    private Integer showInForm;

    @Column("show_in_row_action")
    private Integer showInRowAction;

    @Column("show_in_batch_action")
    private Integer showInBatchAction;

    @Column("action_type")
    private String actionType;

    @Column("sort_no")
    private Integer sortNo;

    @Column("status")
    private String status;
}
