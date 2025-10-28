package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

/**
 * 视图数据传输对象
 *
 * @Author：huangjie
 * @Date：2025/10/24 18:26
 */
@Data
public class ViewDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 实体id
     */
    private Long viewId;

    /**
     * 是否可访问
     */
    private Integer isAllowed;

}