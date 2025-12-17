import { type HostSDK, type Context, type UIAPI } from '../sdk/types';

/**
 * 创建基于宿主的简单 Mock，实现基础上下文与错误上报
 */
export function createMockHostSDK(context: Context, options?: { ui?: UIAPI }): HostSDK {
  const defaultUI: UIAPI = {
    reportError: (error: unknown) => {
      // Mock 环境：控制台输出并携带终端信息
      // eslint-disable-next-line no-console
      console.warn(`[mock-error][${context.terminal}]`, error);
    }
  };

  return {
    context,
    ui: options?.ui ?? defaultUI
  };
}
