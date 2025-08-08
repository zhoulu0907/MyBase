// 验证码类型
export type CaptchaType = 'WORD_IMAGE_CLICK' | 'ROTATE' | 'SLIDER' | 'CONCAT';

// 获取验证码请求参数
export interface GetCaptchaRequest {
  captcha_type: CaptchaType;
}

// 验证码响应数据
export interface CaptchaResponse {
  id: string;
  captcha: {
    type: CaptchaType;
    backgroundImage: string; // base64 编码的背景图片
    templateImage: string; // base64 编码的模板图片
    backgroundImageTag: string;
    backgroundImageWidth: number;
    backgroundImageHeight: number;
    templateImageWidth: number;
    templateImageHeight: number;
  };
}

// 验证验证码请求参数
export interface VerifyCaptchaRequest {
  captcha_id: string;
  captcha_value: string;
  captcha_type: CaptchaType;
}

// 验证验证码响应
export interface VerifyCaptchaResponse {
  verified: boolean;
  message?: string;
}
