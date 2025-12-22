package com.cmsr.onebase.module.dashboard.build.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("dashboard_template")
public class DashboardTemplateDO extends BaseTenantEntity {

    @Column(value = "content", comment = "模板内容")
    private String content;

    // 模板类型
    @Column(value = "template_type", comment = "模板类型")
    private String templateType;

    // 是否热门模板
    @Column(value = "hot", comment = "是否热门模板")
    private Integer hot;

    // 应用ID
    @Column(value = "app_id", comment = "应用ID")
    private Long appId;

    // 索引图片
    @Column(value = "index_image", comment = "索引图片")
    private String indexImage;

}
