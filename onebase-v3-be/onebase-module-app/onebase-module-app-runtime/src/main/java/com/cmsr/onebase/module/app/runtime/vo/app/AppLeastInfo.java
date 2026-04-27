package com.cmsr.onebase.module.app.runtime.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/20 11:23
 */
@Data
public class AppLeastInfo {

    @Schema(description = "应用Id")
    private Long id;

    @Schema(description = "图标名称")
    private String iconName;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "发布模式")
    private String publishModel;


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
