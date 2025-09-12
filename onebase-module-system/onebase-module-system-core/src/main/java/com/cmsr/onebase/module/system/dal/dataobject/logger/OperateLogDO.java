package com.cmsr.onebase.module.system.dal.dataobject.logger;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 操作日志表
 *
 */
@Table(name = "system_operate_log")
@Data
public class OperateLogDO extends TenantBaseDO {

    public static final String TRACE_ID        = "trace_id";
    public static final String USER_ID         = "user_id";
    public static final String USER_TYPE       = "user_type";
    public static final String TYPE            = "type";
    public static final String SUB_TYPE        = "sub_type";
    public static final String BIZ_ID          = "biz_id";
    public static final String ACTION          = "action";
    public static final String EXTRA           = "extra";
    public static final String REQUEST_METHOD  = "request_method";
    public static final String REQUEST_URL     = "request_url";
    public static final String USER_IP         = "user_ip";
    public static final String USER_AGENT      = "user_agent";

    /**
     * 链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    @Column(name = TRACE_ID)
    private String traceId;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 属性，或者 AdminUserDO 的 id 属性
     */
    @Column(name = USER_ID)
    private Long userId;
    /**
     * 用户类型
     *
     * 关联 {@link  UserTypeEnum}
     */
    @Column(name = USER_TYPE)
    private Integer userType;
    /**
     * 操作模块类型
     */
    @Column(name = TYPE)
    private String type;
    /**
     * 操作名
     */
    @Column(name = SUB_TYPE)
    private String subType;
    /**
     * 操作模块业务编号
     */
    @Column(name = BIZ_ID)
    private Long bizId;
    /**
     * 日志内容，记录整个操作的明细
     *
     * 例如说，修改编号为 1 的用户信息，将性别从男改成女，将姓名从OneBase改成源码。
     */
    @Column(name = ACTION)
    private String action;
    /**
     * 拓展字段，有些复杂的业务，需要记录一些字段 ( JSON 格式 )
     *
     * 例如说，记录订单编号，{ orderId: "1"}
     */
    @Column(name = EXTRA)
    private String extra;

    /**
     * 请求方法名
     */
    @Column(name = REQUEST_METHOD)
    private String requestMethod;
    /**
     * 请求地址
     */
    @Column(name = REQUEST_URL)
    private String requestUrl;
    /**
     * 用户 IP
     */
    @Column(name = USER_IP)
    private String userIp;
    /**
     * 浏览器 UA
     */
    @Column(name = USER_AGENT)
    private String userAgent;

}
