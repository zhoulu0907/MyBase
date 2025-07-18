package com.cmsr.onebase.framework.common.anyline.web;

/**
 * @ClassName ErrorCode
 * @Description 系统状态码枚举（结构化改造版）
 * @Author mickey
 * @Date 2025/7/7 10:32
 */
public enum StatusCode {
    // 通用模块
    SERVER_ERROR(Module.COMMON, SubModule.COMMON, 500, "服务器异常"),
    DB_ERROR(Module.COMMON, SubModule.DB, 00, "数据库异常"),
    DB_INSERT_ERROR(Module.COMMON, SubModule.DB, 01, "数据插入失败"),
    DB_UPDATE_ERROR(Module.COMMON, SubModule.DB, 02, "数据更新失败"),
    DB_DELETE_ERROR(Module.COMMON, SubModule.DB, 03, "数据删除失败"),
    DB_SELECT_ERROR(Module.COMMON, SubModule.DB, 04, "数据查询失败"),


    // 租户模块状态码
    TENANT_NOT_EXIST(Module.PLATFORM, SubModule.TENANT, 01, "租户不存在"),
    CREATE_TENANT_FAILED(Module.PLATFORM, SubModule.TENANT, 02, "租户创建失败"),
    UPDATE_TENANT_FAILED(Module.PLATFORM, SubModule.TENANT, 03, "租户更新失败"),
    DELETE_TENANT_FAILED(Module.PLATFORM, SubModule.TENANT, 04, "租户删除失败"),

    // 组织模块状态码
    ORG_NOT_EXIST(Module.PLATFORM, SubModule.ORG, 01, "组织不存在"),
    CREATE_ORG_FAILED(Module.PLATFORM, SubModule.ORG, 02, "组织创建失败"),
    UPDATE_ORG_FAILED(Module.PLATFORM, SubModule.ORG, 03, "组织更新失败"),
    DELETE_ORG_FAILED(Module.PLATFORM, SubModule.ORG, 04, "组织删除失败"),

    // 角色模块状态码
    ROLE_NOT_EXIST(Module.PLATFORM, SubModule.ROLE, 01, "角色不存在"),
    CREATE_ROLE_FAILED(Module.PLATFORM, SubModule.ROLE, 02, "角色创建失败"),
    UPDATE_ROLE_FAILED(Module.PLATFORM, SubModule.ROLE, 03, "角色更新失败"),
    DELETE_ROLE_FAILED(Module.PLATFORM, SubModule.ROLE, 04, "角色删除失败"),

    // 用户模块状态码
    USER_NOT_EXIST(Module.PLATFORM, SubModule.USER, 01, "用户不存在"),
    CREATE_USER_FAILED(Module.PLATFORM, SubModule.USER, 02, "用户创建失败"),
    UPDATE_USER_FAILED(Module.PLATFORM, SubModule.USER, 03, "用户更新失败"),
    DELETE_USER_FAILED(Module.PLATFORM, SubModule.USER, 04, "用户删除失败"),

    // 账户模块状态码
    ACCOUNT_NOT_EXIST(Module.PLATFORM, SubModule.ACCOUNT, 01, "账户不存在"),
    CREATE_ACCOUNT_FAILED(Module.PLATFORM, SubModule.ACCOUNT, 02, "账户创建失败"),
    UPDATE_ACCOUNT_FAILED(Module.PLATFORM, SubModule.ACCOUNT, 03, "账户更新失败"),
    DELETE_ACCOUNT_FAILED(Module.PLATFORM, SubModule.ACCOUNT, 04, "账户删除失败"),
    ACCOUNT_OR_PASSWORD_ERROR(Module.PLATFORM, SubModule.ACCOUNT, 05, "账户或密码错误"),

    // 验证码模块
    VERIFY_CAPTCHA_FAILED(Module.GATEWAY, SubModule.CAPTCHA, 01, "验证码验证失败"),
    VERIFY_CAPTCHA_EXPIRED(Module.GATEWAY, SubModule.CAPTCHA, 02, "验证码已过期"),

    // 鉴权模块
    USER_NO_LOGIN(Module.GATEWAY, SubModule.AUTH, 01, "用户未登录"),
    AUTH_ERROR(Module.GATEWAY, SubModule.AUTH, 02, "登录账号未认证"),
    SM2_KEYPAIR_GEN_FAILED(Module.GATEWAY, SubModule.AUTH, 03, "公钥生成失败"),
    SM2_PRIVATE_KEY_NOT_FOUND(Module.GATEWAY, SubModule.AUTH, 04, "私钥不存在"),
    SM2_DECRYPT_FAILED(Module.GATEWAY, SubModule.AUTH, 05, "SM2解密失败");


    // 模块定义
    public enum Module {
        COMMON(10, "通用模块"),
        PLATFORM(11, "平台管理"),
        GATEWAY(12, "网关模块");


        private final int code;
        private final String name;

        Module(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }
    }

    // 子模块定义
    public enum SubModule {
        COMMON(01, "服务"),
        DB(02, "数据库"),

        TENANT(01, "租户子模块"),
        ORG(02, "组织子模块"),
        ROLE(03, "角色子模块"),
        USER(04, "用户子模块"),
        ACCOUNT(05, "账户子模块"),

        CAPTCHA(01, "验证码模块"),
        AUTH(02, "鉴权模块");

        private final int code;
        private final String name;

        SubModule(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    private final Module module;
    private final SubModule subModule;
    private final int businessCode;
    private final String description;

    StatusCode(Module module, SubModule subModule, int businessCode, String description) {
        this.module = module;
        this.subModule = subModule;
        this.businessCode = businessCode;
        this.description = description;
    }

    /**
     * 获取完整状态码（6位字符串，与原有格式一致）
     */
    public String getCode() {
        return String.format("%02d%02d%02d",
            module.getCode(),
            subModule.getCode(),
            businessCode);
    }

    /**
     * 获取错误描述（与原方法名保持一致）
     */
    public String getDesc() {
        return description;
    }

    /**
     * 根据状态码查找枚举（保持原有功能）
     */
    public static StatusCode statOf(String errCode) {
        for (StatusCode code : values()) {
            if (code.getCode().equals(errCode)) {
                return code;
            }
        }
        return null;
    }

    // 新增方法：获取模块信息
    public Module getModule() {
        return module;
    }

    // 新增方法：获取子模块信息
    public SubModule getSubModule() {
        return subModule;
    }

    @Override
    public String toString() {
        return getCode() + ":" + description;
    }
}
