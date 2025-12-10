package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:54
 */
@Data
@Table(value = "app_version")
public class AppVersionDO extends BaseAppEntity {

    @Column(value = "version_name", comment = "版本名称")
    private String versionName;

    @Column(value = "version_number", comment = "版本号")
    private String versionNumber;

    @Column(value = "version_description", comment = "版本描述")
    private String versionDescription;

    @Column(value = "environment", comment = "环境")
    private String environment;

    @Column(value = "operation_type", comment = "操作类型")
    private Integer operationType;

    @Column(value = "version_url", comment = "版本URL")
    private String versionURL;

    @Column(value = "version_type", comment = "版本类型")
    private Integer versionTpye;

}
