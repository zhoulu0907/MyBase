import { forwardRef, useImperativeHandle } from 'react';

// 扩展 Window 接口以包含 initTAC 方法
declare global {
  interface Window {
    initTAC: (path: string, config: any, style: any) => Promise<any>;
  }
}

/**
 * Captcha 组件
 *
 * 用于渲染第三方滑块/点选验证码（如 TAC），通过外部 script 加载。
 *
 * Props:
 * - requestCaptchaDataUrl: 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
 * - validCaptchaUrl: 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
 * - bindEl: 验证码绑定的div块 (必选项,必须配置)
 * - options: 传递给验证码初始化的其他参数对象
 * - onSuccess: 验证成功回调
 * - onError: 验证失败回调
 */
export interface CaptchaProps {
  requestCaptchaDataUrl: string;
  validCaptchaUrl: string;
  bindEl: string;
  options?: Record<string, any>;
  onSuccess: (data: any) => void;
  onError?: (err: any) => void;
}

export interface CaptchaRef {
  checkCaptcha: () => void;
}

const DEFAULT_BIND_EL = 'captcha-box';

const Captcha = forwardRef<CaptchaRef, CaptchaProps>(
  ({ requestCaptchaDataUrl, validCaptchaUrl, bindEl = DEFAULT_BIND_EL, options = {}, onSuccess, onError }, ref) => {
    const checkCaptcha = () => {
      // 随机生成验证码类型
      const datasetType = ['SLIDER', 'ROTATE', 'CONCAT', 'WORD_IMAGE_CLICK'][Math.floor(Math.random() * 4)];
      // config 对象为TAC验证码的一些配置和验证的回调
      const config = {
        datasetType: datasetType,
        // 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
        requestCaptchaDataUrl: requestCaptchaDataUrl + '?captcha_type=' + datasetType,
        // 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
        validCaptchaUrl: validCaptchaUrl,
        // 验证码绑定的div块 (必选项,必须配置)
        bindEl: '#' + bindEl,
        // 验证成功回调函数(必选项,必须配置)
        validSuccess: (res: any, c: any, tac: any) => {
          // 销毁验证码服务
          tac.destroyWindow();
          console.log('验证成功，后端返回的数据为', res);
          onSuccess(res.token);
        },
        // 验证失败的回调函数(可忽略，如果不自定义 validFail 方法时，会使用默认的)
        validFail: (res: any, c: any, tac: any) => {
          console.log('验证码验证失败回调...');
          // 验证失败后重新拉取验证码
          tac.reloadCaptcha();
        },
        // 刷新按钮回调事件
        btnRefreshFun: (el: any, tac: any) => {
          console.log('刷新按钮触发事件...');
          tac.reloadCaptcha();
        },
        // 关闭按钮回调事件
        btnCloseFun: (el: any, tac: any) => {
          console.log('关闭按钮触发事件...');
          tac.destroyWindow();
        }
      };

      let style = {
        logoUrl: null
      };

      window
        .initTAC('./tac', config, style)
        .then((tac: any) => {
          tac.init(); // 调用init则显示验证码
        })
        .catch((e: any) => {
          console.log('初始化tac失败', e);
        });
    };

    useImperativeHandle(ref, () => ({
      checkCaptcha
    }));

    return (
      <div>
        <div id={bindEl}></div>
      </div>
    );
  }
);

Captcha.displayName = 'Captcha';

export default Captcha;
