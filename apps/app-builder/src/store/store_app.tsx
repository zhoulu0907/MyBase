import { create } from 'zustand';
import { AppStatus } from '@onebase/app';

interface CurAppInfo {
  iconName: string;
  iconColor: string;
  appName: string;
  appStatus: AppStatus;
}
export interface appStore {
  // 当前应用的appCode
  curAppId: string;
  // 当前应用的信息
  curAppInfo: CurAppInfo;
  // 设置当前应用的appCode
  setCurAppId: (appId: string) => void;
  // 设置当前应用信息
  setCurAppInfo: (appInfo: CurAppInfo) => void;
  // 清除当前应用的appCode
  clearCurAppId: () => void;
}

export const useAppStore = create<appStore>((set) => ({
  curAppId: '',
  curAppInfo: {
    iconName: '',
    iconColor: '',
    appName: '--',
    appStatus: 0
  },

  setCurAppId: (appId: string) => set(() => ({ curAppId: appId })),
  setCurAppInfo: (appInfo: CurAppInfo) => set(() => ({ curAppInfo: appInfo })),
  clearCurAppId: () => set(() => ({ curAppId: '' }))
}));
