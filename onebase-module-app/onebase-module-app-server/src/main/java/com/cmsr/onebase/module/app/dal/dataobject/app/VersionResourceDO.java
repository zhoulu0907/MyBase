package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 16:54
 */
@Data
@Table(name = "app_version_resource")
public class VersionResourceDO extends BaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用ID")
    private Long applicationId;

    @Column(name = "version_id", nullable = false, comment = "版本ID")
    private Long versionId;

    @Column(name = "protocol_type", nullable = false, length = 64, comment = "协议类型")
    private String protocolType;

    @Column(name = "res_key", nullable = false, length = 64, comment = "资源key")
    private String resKey;

    @Column(name = "res_data", nullable = false, columnDefinition = "text", comment = "资源数据")
    private String resData;
}