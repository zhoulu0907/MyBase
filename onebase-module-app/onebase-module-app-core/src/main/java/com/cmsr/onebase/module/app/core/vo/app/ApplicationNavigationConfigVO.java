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


    /**
     * appThirdUserEnable 0-不启用第三方登录 1-启用第三方登录
     */
    @Schema(description = "启用三方登录")
    private String appThirdUserEnable;

    /**
     * 0-不显示 1-显示
     */
    @Schema(description = "显示注册入口")
    private String appUserRegisterShow;
    /**
     *  0-不显示 1-显示
     */
    @Schema(description = "显示忘记密码入口")
    private String appUserForgetPwdShow;
    /**
     * 登录页主图，文件ID
     */
    @Schema(description = "登录页主图")
    private String appLoginMainPic;

}