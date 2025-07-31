package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 15:00
 */
@Data
@Table(name = "app_application_resource")
public class ApplicationResourceDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用ID")
    private Long applicationId;

    @Column(name = "protocol_type", nullable = false, length = 64, comment = "协议类型")
    private String protocolType;

    @Column(name = "res_key", nullable = false, length = 64, comment = "资源key")
    private String resKey;

    @Column(name = "res_data", nullable = false, columnDefinition = "text", comment = "资源数据")
    private String resData;

}