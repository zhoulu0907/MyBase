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

export interface thirdUserRegisterParams {
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

  /**
   * 设备ID
   */
  deviceId: string;
}

export interface loginConfigParams {
  /**
   * AppId称不能为空
   */
  appId: string;
  /**
   * 批量查询keys
   */
  configKeys: string[];
}

export interface loginPermissionRes {
  /**
   * 参数键名
   * 配置项
   */
  configKey?: string;
  /**
   * 参数分类 see{@link ConfigTypeEnum}
   * 参数分类
   */
  configType?: string;
  /**
   * 参数键值
   * 配置项值
   */
  configValue?: string;
  /**
   * 归属企业ID
   * 企业id
   */
  corpId?: string;
  /**
   * 互斥项
   */
  exclusiveItem?: string;
  /**
   * 参数分类
   */
  id?: string;
  /**
   * 参数名称
   * 名称
   */
  name?: string;
  /**
   * 备注
   */
  remark?: string;
  /**
   * 参数类型
   *
   * 枚举
   * 状态
   */
  status?: number;
}

/**
 * SystemGeneralConfigUpdateReqVO
 */
export interface updateLoginConfigParams {
  /**
   * appId
   */
  appId?: string;
  /**
   * key
   */
  configKey?: string;
  /**
   * 参数键值
   */
  configValue?: string;
  /**
   * 参数分类
   */
  id?: string;
  /**
   * 参数名称
   */
  name?: string;
  /**
   * 备注
   */
  remark?: string;
}

export interface createExternalUserAppParams {
  /**
   * 应用id
   */
  applicationIdList: string[];
  /**
   * 用户Id
   */
  userId: string;

  email?: string;
  nickName?: string;
}

export interface forgotPWDParams {
    /**
     * 手机号
     */
    mobile: string;
    /**
     * 密码
     */
    password: string;
    /**
     * 验证码
     */
    verifyCode: string;
}