package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 16:54
 */
@Data
@Table(value = "app_version_resource")
public class AppVersionResourceDO extends BaseAppEntity {

    @Column(value = "version_id", comment = "版本ID")
    private Long versionId;

    @Column(value = "res_type", comment = "协议类型")
    private String resType;

    @Column(value = "res_data", comment = "资源数据")
    private String resData;
}
