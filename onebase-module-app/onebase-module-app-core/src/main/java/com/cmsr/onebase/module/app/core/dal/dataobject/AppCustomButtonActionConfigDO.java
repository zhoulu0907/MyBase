package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table("app_custom_button_action_config")
@EqualsAndHashCode(callSuper = true)
public class AppCustomButtonActionConfigDO extends BaseBizEntity {

    @Column("button_uuid")
    private String buttonUuid;

    @Column("action_type")
    private String actionType;

    @Column("open_mode")
    private String openMode;

    @Column("submit_success_text")
    private String submitSuccessText;

    @Column("target_type")
    private String targetType;

    @Column("target_pageset_uuid")
    private String targetPageSetUuid;

    @Column("target_page_uuid")
    private String targetPageUuid;

    @Column("target_url")
    private String targetUrl;

    @Column("target_entity_uuid")
    private String targetEntityUuid;

    @Column("target_relation_field_uuid")
    private String targetRelationFieldUuid;

    @Column("target_relation_scope")
    private String targetRelationScope;

    @Column("flow_process_id")
    private Long flowProcessId;

    @Column("flow_process_uuid")
    private String flowProcessUuid;

    @Column("confirm_required")
    private Integer confirmRequired;

    @Column("confirm_text")
    private String confirmText;

    @Column("config_json")
    private String configJson;
}
