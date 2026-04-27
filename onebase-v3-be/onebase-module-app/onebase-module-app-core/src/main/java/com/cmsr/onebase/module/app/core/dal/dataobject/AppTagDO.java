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
@Table(value = "app_tag")
public class AppTagDO extends BaseTenantEntity {

    @Column(value = "tag_name", comment = "标签名称")
    private String tagName;

}