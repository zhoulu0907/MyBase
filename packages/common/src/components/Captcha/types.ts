/**
 * 获取登录验证码的参数
 */
export interface Captcha {
    captchaType: string,
    clientUid?: string
  }

/**
 * 验证验证码的参数
 */
export interface CaptchaCheck {
    captchaType: string;
    pointJson: string;
    token: string
}
