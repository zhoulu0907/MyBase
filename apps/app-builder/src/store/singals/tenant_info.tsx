import type { TenantInfo } from '@onebase/platform-center';
import { signal } from '@preact/signals-react';

const createTenantInfoSignal = () => {
  const tenantInfo = signal<TenantInfo | null>(null);
  const setTenantInfo = (value: TenantInfo) => {
    tenantInfo.value = value;
  };
  return {
    tenantInfo,
    setTenantInfo
  };
};

// 创建默认的 store 实例（向后兼容）
export const tenantInfoSignal = createTenantInfoSignal();
