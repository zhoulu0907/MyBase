package com.cmsr.onebase.module.infra.enums.security;


import lombok.Getter;
import java.util.Optional;

/**
 * 安全配置模板键值枚举
 */
public enum SecurityConfigKey {

    // 密码策略
    minLength("minLength", "密码最小长度"),
    extraCharacter("extraCharacter", "额外字符类型要求", ExtraCharacterOption.class),
    expiryDays("expiryDays", "密码有效期(天)"),
    reminderDays("reminderDays", "到期提醒天数"),
    historyLimit("historyLimit", "历史密码限制"),
    enableWeakPassword("enableWeakPassword", "弱密码拦截"),

    // 账号锁定策略
    failedLockThreshold("failedLockThreshold", "连续失败锁定阈值"),
    lockDuration("lockDuration", "锁定时长(分钟)"),
    unlockMethod("unlockMethod", "锁定后解锁方式", UnlockMethodOption.class),

    // 验证码策略
    enableScenarios("enableScenarios", "启用场景", EnableScenariosOption.class),
    type("type", "验证码类型", CaptchaTypeOption.class),
    difficulty("difficulty", "验证码难度", CaptchaDifficultyOption.class),
    expirySeconds("expirySeconds", "验证码有效期(秒)"),
    refreshInterval("refreshInterval", "刷新间隔(秒)"),

    // 会话与设备安全
    maxOnlineDevices("maxOnlineDevices", "最大同时在线设备数"),
    newDeviceLoginAlert("newDeviceLoginAlert", "新设备登录提醒"),
    newDeviceTwoFactor("newDeviceTwoFactor", "新设备登录二次验证"),
    remoteLoginDetection("remoteLoginDetection", "异地登录检测"),
    sessionTimeout("sessionTimeout", "会话超时时间(秒)"),

    // MFA 多因素认证
    supportedTypes("supportedTypes", "支持的MFA类型", SupportedMfaTypesOption.class),
    triggerScenarios("triggerScenarios", "触发MFA的场景", MfaTriggerScenariosOption.class),
    backupCodeCount("backupCodeCount", "备用验证码数量"),

    // OAuth2/OpenID 配置
    globalEnabled("globalEnabled", "是否开启OAuth2/OpenID登录"),
    globalAutoCreateAccount("globalAutoCreateAccount", "首次登录自动创建账号"),
    globalLoginSectionTitle("globalLoginSectionTitle", "登录页第三方登录板块标题"),
    globalallowBindExisting("globalallowBindExisting", "允许绑定到已有账号"),
    providerEnabled("providerEnabled", "是否启用该登录源"),
    providerIsDefault("providerIsDefault", "设为默认登录源"),
    providerButtonText("providerButtonText", "登录按钮文字"),
    providerProtocolType("providerProtocolType", "协议类型", ProtocolTypeOption.class),
    providerClientId("providerClientId", "Client ID"),
    providerClientSecret("providerClientSecret", "Client Secret"),
    providerAuthorizationEndpoint("providerAuthorizationEndpoint", "授权端点URL"),
    providerTokenEndpoint("providerTokenEndpoint", "Token端点URL"),
    providerUserInfoEndpoint("providerUserInfoEndpoint", "用户信息端点URL"),
    providerRedirectUriPrefix("providerRedirectUriPrefix", "回调地址前缀"),
    providerUniqueIdentifierMapping("providerUniqueIdentifierMapping", "唯一标识映射"),
    providerButtonIcon("providerButtonIcon", "登录按钮图标"),
    providerSortOrder("providerSortOrder", "排序号"),
    providerScopes("providerScopes", "权限范围(Scope)"),
    providerUsernameMapping("providerUsernameMapping", "用户名字段映射"),
    providerEmailMapping("providerEmailMapping", "邮箱字段映射"),
    providerClientAuthenticationMethod("providerClientAuthenticationMethod", "客户端认证方式", ClientAuthMethodOption.class),

    //文件上传校验策略
    uploadFileLengthLimit("uploadFileLengthLimit", "文件上传大小限制（MB）"),
    uploadFileNameLengthLimit("uploadFileNameLengthLimit", "文件名长度限制"),
    uploadFileCheckList("uploadFileCheckList", "上传文件检查项");

    @Getter
    private final String configKey;
    @Getter
    private final String configName;
    private final Class<? extends OptionEnum> optionClass;

    SecurityConfigKey(String configKey, String configName) {
        this(configKey, configName, null);
    }

    SecurityConfigKey(String configKey, String configName, Class<? extends OptionEnum> optionClass) {
        this.configKey = configKey;
        this.configName = configName;
        this.optionClass = optionClass;
    }

    public Optional<Class<? extends OptionEnum>> getOptionClass() {
        return Optional.ofNullable(optionClass);
    }

    // ====== 嵌套选项枚举定义（仅 key，不做 value 映射） ======

    public interface OptionEnum {
        String getKey();
    }

    public enum ExtraCharacterOption implements OptionEnum {
        upperCase("upperCase"),
        specialChar("specialChar");

        private final String key;
        ExtraCharacterOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum UnlockMethodOption implements OptionEnum {
        auto("auto"),
        captcha("captcha");

        private final String key;
        UnlockMethodOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum EnableScenariosOption implements OptionEnum {
        login("login"),
        pwdreset("pwdreset"),
        register("register"),
        unlock("unlock"),
        bind("bind");

        private final String key;
        EnableScenariosOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum CaptchaTypeOption implements OptionEnum {
        slider("slider"),
        captcha("captcha");

        private final String key;
        CaptchaTypeOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum CaptchaDifficultyOption implements OptionEnum {
        low("low"),
        medium("medium"),
        high("high");

        private final String key;
        CaptchaDifficultyOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum SupportedMfaTypesOption implements OptionEnum {
        totp("totp"),
        phone("phone"),
        email("email");

        private final String key;
        SupportedMfaTypesOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum MfaTriggerScenariosOption implements OptionEnum {
        sensitive("sensitive"),
        newDevice("newDevice"),
        login("login");

        private final String key;
        MfaTriggerScenariosOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum ProtocolTypeOption implements OptionEnum {
        OAuth2("OAuth2"),
        OpenIdConnect("OpenIdConnect");

        private final String key;
        ProtocolTypeOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }

    public enum ClientAuthMethodOption implements OptionEnum {
        client_secret_basic("client_secret_basic"),
        client_secret_post("client_secret_post"),
        none("none");

        private final String key;
        ClientAuthMethodOption(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }
}