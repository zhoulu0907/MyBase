package com.cmsr.onebase.module.system.dal.dataobject.notify;

import java.util.List;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 站内信模版 DO
 *
 * @author xrcoder
 */
@Table(name = "system_notify_template")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class NotifyTemplateDO extends BaseDO {

    // 字段常量
    public static final String NAME    = "name";
    public static final String CODE    = "code";
    public static final String TYPE    = "type";
    public static final String NICKNAME= "nickname";
    public static final String CONTENT = "content";
    public static final String PARAMS  = "params";
    public static final String STATUS  = "status";
    public static final String REMARK  = "remark";

    /**
     * 模版名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 模版编码
     */
    @Column(name = CODE)
    private String code;
    /**
     * 模版类型
     *
     * 对应 system_notify_template_type 字典
     */
    @Column(name = TYPE)
    private Integer type;
    /**
     * 发送人名称
     */
    @Column(name = NICKNAME)
    private String nickname;
    /**
     * 模版内容
     */
    @Column(name = CONTENT)
    private String content;
    /**
     * 参数数组
     */
    @Column(name = PARAMS)
    private List<String> params;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

}
