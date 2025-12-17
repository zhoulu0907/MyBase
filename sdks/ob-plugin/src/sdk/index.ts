import { type Context, type HostSDK, type UIAPI } from './types';

/**
 * 创建一个最小可用的 SDK 实例（供插件调用）。
 * 宿主在生产环境应替换 ui 的具体实现。
 */
export function createHostSDK(context: Context, overrides?: { ui?: UIAPI }): HostSDK {
  const defaultUI: UIAPI = {
    reportError: (error: unknown) => {
      // 默认仅输出到控制台，宿主可替换为真实上报
      // eslint-disable-next-line no-console
      console.error('[plugin-error]', error);
    }
  };
  
  return {
    context,
    ui: overrides?.ui ?? defaultUI
  };
}

export * from './types';
