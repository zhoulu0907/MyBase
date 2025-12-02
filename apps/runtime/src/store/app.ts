import type { Application } from '@onebase/app';
import { signal } from '@preact/signals-react';

export const createAppInofSignal = () => {
  const curAppInfo = signal<Application>({} as Application);
  const setCurAppInfo = (menu: Application) => {
    curAppInfo.value = menu;
  };

  return {
    curAppInfo,
    setCurAppInfo
  };
};

// 创建默认的 store 实例（向后兼容）
export const appInfoSignal = createAppInofSignal();
