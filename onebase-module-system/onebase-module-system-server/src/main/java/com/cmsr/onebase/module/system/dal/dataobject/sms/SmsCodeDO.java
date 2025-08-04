package com.cmsr.onebase.module.system.dal.dataobject.sms;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 手机验证码 DO
 *
 * idx_mobile 索引：基于 {@link #mobile} 字段
 *
 */
@Table(name = "system_sms_code")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class SmsCodeDO extends BaseDO {

    // 字段列名常量
    public static final String MOBILE      = "mobile";
    public static final String CODE        = "code";
    public static final String SCENE       = "scene";
    public static final String CREATE_IP   = "create_ip";
    public static final String TODAY_INDEX = "today_index";
    public static final String USED        = "used";
    public static final String USED_TIME   = "used_time";
    public static final String USED_IP     = "used_ip";

    /**
     * 手机号
     */
    @Column(name = MOBILE)
    private String mobile;
    /**
     * 验证码
     */
    @Column(name = CODE)
    private String code;
    /**
     * 发送场景
     */
    @Column(name = SCENE)
    private Integer scene;
    /**
     * 创建 IP
     */
    @Column(name = CREATE_IP)
    private String createIp;
    /**
     * 今日发送的第几条
     */
    @Column(name = TODAY_INDEX)
    private Integer todayIndex;
    /**
     * 是否使用
     */
    @Column(name = USED)
    private Boolean used;
    /**
     * 使用时间
     */
    @Column(name = USED_TIME)
    private LocalDateTime usedTime;
    /**
     * 使用 IP
     */
    @Column(name = USED_IP)
    private String usedIp;
}
