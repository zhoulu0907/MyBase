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
@Table(name = "app_tag")
public class TagDO extends BaseDO {

    @Column(name = "tag_name", nullable = false, length = 128, comment = "标签名称")
    private String tagName;

}