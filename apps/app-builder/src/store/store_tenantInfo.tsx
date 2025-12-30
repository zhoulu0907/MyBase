import { create } from 'zustand';
import { type TenantInfo } from '@onebase/platform-center';
import { getTenantInfoFromSession, setTenantInfoFromSession } from '@/utils';

type TenantInfoState = {
  curTenantInfo: TenantInfo | null;
  setTenantInfo: (info: TenantInfo | null) => void;
};

export const useTenantInfoStore = create<TenantInfoState>((set) => ({
  // 初始化时从 session 里读
  curTenantInfo: getTenantInfoFromSession(),
  setTenantInfo: (info: TenantInfo) => {
    set(() => ({ curTenantInfo: info }));
    setTenantInfoFromSession(info); // 继续保持和 session 同步
  }
}));
