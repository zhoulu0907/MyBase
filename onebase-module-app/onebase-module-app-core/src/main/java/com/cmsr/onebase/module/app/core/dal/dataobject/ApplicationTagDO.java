package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(value = "app_application_tag")
public class ApplicationTagDO extends BaseAppEntity {

    @Column(value = "tag_id", comment = "标签ID")
    private Long tagId;


}
