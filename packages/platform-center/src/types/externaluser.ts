export interface createExternalUserParams {
    /**
     * 应用id
     */
    applicationIdList: string[];
    /**
     * 用户头像
     */
    avatar?: string;
    /**
     * 用户邮箱
     */
    email?: string;
    /**
     * 手机
     */
    mobile: string;
    nickname: string;
    /**
     * 状态
     */
    status?: number;
}

export interface updateExternalUserParams {
    /**
     * 应用id
     */
    applicationIdList?: string[];
    /**
     * 用户头像
     */
    avatar?: string;
    /**
     * 用户邮箱
     */
    email?: string;
    /**
     * 手机
     */
    mobile?: string;
    nickname?: string;
    /**
     * 状态
     */
    status?: number;
    /**
     * 用户id
     */
    userId: string;
}

export interface externalUserListParams {
    /**
     * 页码，从 1 开始
     */
    pageNo: number;
    /**
     * 每页条数，最大值为 100
     */
    pageSize: number;
    /**
     * 状态
     */
    status?: number;
    /**
     * 用户ID
     */
    userIds?: string[];
    /**
     * 用户名称
     */
    userName?: string;
    deptId?: string;
}

export interface updateExternalPwdParams {
    /**
     * 用户编号
     */
    id: string;
    /**
     * 密码
     */
    password?: string;
}

export interface updateStatusParams {
    /**
     * 用户编号
     */
    id: string;
    /**
     * 状态，见 CommonStatusEnum 枚举
     */
    status: number;
}

export interface pluginParams {
    name?: string;
    status?: number | null;
}

export interface updatePasswordParams {
    /**
     * 手机号
     */
    mobile: string;
    /**
     * 密码
     */
    password?: string;
    /**
     * 验证码
     */
    verifyCode: string;
}

export interface registerExternalUserParams {
    /**
     * 用户名称
     */
    appId?: number;
    /**
     * 手机
     */
    mobile?: string;
    /**
     * 验证码
     */
    verifyCode?: string;
}

export interface supplementUserInfoParams {
    appId: string;
    /**
     * 用户头像
     */
    avatar?: string;
    /**
     * 用户邮箱
     */
    email?: string;
    nickName: string;
    /**
     * 密码
     */
    password: string;
    /**
     * 手机号
     */
    mobile: string;
    /**
     * 用户账号
     */
    userName?: string;
}