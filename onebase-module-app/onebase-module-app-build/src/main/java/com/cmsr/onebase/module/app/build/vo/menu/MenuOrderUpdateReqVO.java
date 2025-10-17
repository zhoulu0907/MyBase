package com.cmsr.onebase.module.app.build.vo.menu;

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

    @Schema(description = "变更菜单Id")
    private Long id;

    @Schema(description = "父菜单Id，如果变成根菜单，设置为0")
    private Long parentId;

    @Schema(description = "菜单顺序树结构")
    private List<MenuOrderNode> menuTree;

    @Data
    public static class MenuOrderNode {

        private Long id;

        private List<MenuOrderNode> children;
    }

}
