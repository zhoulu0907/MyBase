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
    status?: number;
}