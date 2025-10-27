package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 18:26
 */
@Data
public class ViewDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "实体id")
    private Long viewId;

    @Schema(description = "是否可访问")
    private Integer isAllowed;

}
