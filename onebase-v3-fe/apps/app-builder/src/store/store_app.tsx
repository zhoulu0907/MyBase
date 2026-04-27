import { create } from 'zustand';
import { AppStatus, type Application } from '@onebase/app';

export interface appStore {
  // 当前应用的appCode
  curAppId: string;
  // 当前应用的信息
  curAppInfo: Application;
  // 设置当前应用的appCode
  setCurAppId: (appId: string) => void;
  // 设置当前应用信息
  setCurAppInfo: (appInfo: Application) => void;
  // 清除当前应用的appCode
  clearCurAppId: () => void;
}

export const useAppStore = create<appStore>((set) => ({
  curAppId: '',
  curAppInfo: {
    id: '',
    appName: '--',
    appCode: '',
    appStatus: 0,
  },

  setCurAppId: (appId: string) => set(() => ({ curAppId: appId })),
  setCurAppInfo: (appInfo: Application) => set(() => ({ curAppInfo: appInfo })),
  clearCurAppId: () => set(() => ({ curAppId: '' }))
}));
