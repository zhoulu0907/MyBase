package com.cmsr.onebase.module.tiangong.dal.dataflexdo;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 天工提醒数据 DO
 *
 * @author matianyu
 * @date 2026-04-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = TGAlertDO.TABLE_NAME)
public class TGAlertDO extends BaseEntity {

    public static final String TABLE_NAME = "tiangong_alert";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_LINK_URL = "link_url";
    public static final String FIELD_CLICKED = "clicked";

    /**
     * 提醒标题
     */
    @Column(name = FIELD_TITLE, comment = "提醒标题")
    private String title;

    /**
     * 提醒内容
     */
    @Column(name = FIELD_CONTENT, comment = "提醒内容")
    private String content;

    /**
     * 提醒跳转链接
     */
    @Column(name = FIELD_LINK_URL, comment = "提醒跳转链接")
    private String linkUrl;

    /**
     * 是否已读：0 未读，1 已读
     */
    @Column(name = FIELD_CLICKED, comment = "是否已读")
    private Integer clicked;
}

