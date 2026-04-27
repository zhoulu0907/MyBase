export enum thirdUserConfigKey {
    ENABLE = 'appThirdUserEnable',
    REGISTER_SHOW = 'appThirdUserRegisterShow',
    FORGOT_PWD = 'appThirdUserForgetPwdShow'
}

export enum ThirdLoginType {
  PASSWORD = 'password',
  VERIFYCODE = 'verifycode'
}

export enum ThirdLoginTypeLabel {
  PASSWORD = '密码登录',
  VERIFYCODE = '验证码登录'
}

export const ThirdLoginMap = [
  {label: ThirdLoginTypeLabel.VERIFYCODE, value: ThirdLoginType.VERIFYCODE},
  {label: ThirdLoginTypeLabel.PASSWORD, value: ThirdLoginType.PASSWORD}
]