package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(value = "app_application")
public class AppApplicationDO extends BaseTenantEntity {

    @Column(value = "app_uid", comment = "应用uid(自动生成短码)")
    private String appUid;

    @Column(value = "app_name", comment = "应用名称")
    private String appName;

    @Column(value = "app_code", comment = "应用编码")
    private String appCode;

    @Column(value = "app_mode", comment = "应用模式")
    private String appMode;

    @Column(value = "version_url", comment = "版本URL")
    private String versionUrl;

    @Column(value = "app_status", comment = "状态")
    private Integer appStatus;

    @Column(value = "description", comment = "描述")
    private String description;

    @Column(value = "publish_model", comment = "发布模式/内部模式")
    private String publishModel;

    /**
     * 发布状态，0从未发布 1发布过
     */
    @Column(value = "publish_status", comment = "发布状态")
    private Integer publishStatus;

}
