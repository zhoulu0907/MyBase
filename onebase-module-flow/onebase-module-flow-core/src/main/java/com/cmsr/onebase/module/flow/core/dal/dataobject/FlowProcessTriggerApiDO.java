package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_trigger_api")
public class FlowProcessTriggerApiDO extends TenantBaseDO {
    /**
     * 主键ID
     */
    @Column(name = "id", length = 19, nullable = false)
    private Long id;
    /**
     * 流程ID
     */
    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;
    /**
     * 节点ID
     */
    @Column(name = "node_id", length = 64, nullable = false)
    private String nodeId;
    /**
     * API路径
     */
    @Column(name = "url_path", length = 128)
    private String urlPath;
    /**
     * HTTP方法 'GET','POST','PUT','DELETE'
     */
    @Column(name = "http_method", length = 64)
    private String httpMethod;
    /**
     * 认证方式，无、Basic、Bearer
     */
    @Column(name = "auth_type", length = 64)
    private String authType;
    /**
     * 认证key
     */
    @Column(name = "auth_key", length = 128)
    private String authKey;
    /**
     * 请求参数结构定义
     */
    @Column(name = "request_params_schema", length = 2147483647)
    private String requestParamsSchema;
    /**
     * 响应结果结构定义
     */
    @Column(name = "response_schema", length = 2147483647)
    private String responseSchema;
    /**
     * 响应字段映射
     */
    @Column(name = "response_mapping", length = 2147483647)
    private String responseMapping;
    /**
     * 请求频率限制单位，每分钟、每秒钟
     */
    @Column(name = "rate_limit_unit", length = 64)
    private String rateLimitUnit;
    /**
     * 请求频率限制值
     */
    @Column(name = "rate_limit_count", length = 19)
    private Integer rateLimitCount;

}