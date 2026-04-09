package com.cmsr.onebase.module.app.api.version.dto;

import lombok.Data;

/**
 * @Author：matianyu
 * @Date：2026/03/18
 */
@Data
public class AppNavigationDTO {


    private Long applicationId;

    private Long versionTag;

    private String themeColor;

    private String iconName;

    private String iconColor;

    private String webDefaultMenu;

    private String webNavLayout;

    private String mobileDefaultMenu;

    private String mobileNavLayout;

    /**
     * appThirdUserEnable 0-不启用第三方登录 1-启用第三方登录
     */
    private String appThirdUserEnable;

    /**
     * 0-不显示 1-显示
     */
    private String appUserRegisterShow;
    /**
     * 0-不显示 1-显示
     */
    private String appUserForgetPwdShow;
    /**
     * 登录页主图，文件ID
     */
    private String appLoginMainPic;


}
