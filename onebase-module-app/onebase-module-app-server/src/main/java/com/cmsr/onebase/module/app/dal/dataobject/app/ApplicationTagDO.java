package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 17:50
 */
@Data
@Table(name = "app_application_tag")
public class ApplicationTagDO extends BaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用ID")
    private Long applicationId;

    @Column(name = "app_code", nullable = false, comment = "标签ID")
    private Long tagId;


}