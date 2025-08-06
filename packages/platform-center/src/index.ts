// 导出类型定义
export * from './types';

export { getBackendURL } from './services/base';

// 导出验证码服务
export { CaptchaService, captchaService, getCaptcha, verifyCaptcha } from './services/captcha';

// 导出会话服务
export * from './services/auth';

export * from './services/dict';

export * from './services/user';

export * from './services/dept';

export * from './services/platformInfo';