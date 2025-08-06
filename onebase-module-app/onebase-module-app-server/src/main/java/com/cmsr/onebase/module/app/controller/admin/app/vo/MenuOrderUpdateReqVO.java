package com.cmsr.onebase.module.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/31 8:26
 */
@Schema(description = "应用管理 - 应用菜单更新顺序 Resp VO")
@Data
public class MenuOrderUpdateReqVO {

    private Long id;

    private Long parentId;

    @Schema(description = "菜单顺序树结构")
    private List<MenuOrderNode> menuTree;

    @Data
    public static class MenuOrderNode {

        private Long id;

        private List<MenuOrderNode> children;
    }

}
