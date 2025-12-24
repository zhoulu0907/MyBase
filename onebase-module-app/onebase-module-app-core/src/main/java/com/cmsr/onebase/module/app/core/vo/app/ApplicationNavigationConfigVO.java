package com.cmsr.onebase.module.app.core.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/13
 */
@Schema(description = "应用管理 - 应用导航配置 Response VO")
@Data
public class ApplicationNavigationConfigVO {

    @Schema(description = "应用ID")
    private Long id;

    @Schema(description = "web端默认首页菜单")
    private String webDefaultMenu;

    @Schema(description = "web端导航布局")
    private String webNavLayout;

    @Schema(description = "移动端默认首页菜单") 
    private String mobileDefaultMenu;

    @Schema(description = "移动端导航布局")
    private String mobileNavLayout;

}