package com.cmsr.onebase.module.system.dal.dataobject.notice;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.module.system.enums.notice.NoticeTypeEnum;
import lombok.Data;

/**
 * 通知公告表
 *
 * @author ruoyi
 */
@Table(name = "system_notice")
@Data
public class NoticeDO extends BaseDO {

    public static final String TITLE   = "title";
    public static final String TYPE    = "type";
    public static final String CONTENT = "content";
    public static final String STATUS  = "status";

    /**
     * 公告标题
     */
    @Column(name = TITLE)
    private String title;
    /**
     * 公告类型
     *
     * 枚举 {@link NoticeTypeEnum}
     */
    @Column(name = TYPE)
    private Integer type;
    /**
     * 公告内容
     */
    @Column(name = CONTENT)
    private String content;
    /**
     * 公告状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;

}
