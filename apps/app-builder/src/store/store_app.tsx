import { create } from 'zustand';

export interface appStore {
  // 当前应用的appCode
  curAppId: string;
  // 设置当前应用的appCode
  setCurAppId: (appId: string) => void;
  // 清除当前应用的appCode
  clearCurAppId: () => void;
}

export const useAppStore = create<appStore>((set) => ({
  curAppId: '',
  setCurAppId: (appId: string) => set(() => ({ curAppId: appId })),
  clearCurAppId: () => set(() => ({ curAppId: '' }))
}));
