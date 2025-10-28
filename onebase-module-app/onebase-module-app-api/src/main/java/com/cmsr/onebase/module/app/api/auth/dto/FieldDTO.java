package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

/**
 * 应用字段权限定义
 *
 * @Author：huangjie
 * @Date：2025/10/24 17:56
 */
@Data
public class FieldDTO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 字段id
     */
    private Long fieldId;

    /**
     * 是否可阅读
     */
    private Integer isCanRead;

    /**
     * 是否可编辑
     */
    private Integer isCanEdit;

    /**
     * 是否可下载
     */
    private Integer isCanDownload;

}