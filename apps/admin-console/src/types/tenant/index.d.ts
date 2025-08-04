// types/tenant.ts
export type TenantStatus = 'enabled' | 'disabled';

export interface Tenant {
  id: string;
  name: string;
  code: string;
  allocatedCount: number;
  admin: string;
  createTime: string;
  status: TenantStatus;
}

export interface TenantRecord {
  id: number;
  tenantName: string;
  tenantCode: string;
  allocatedCount: number;
  admin: string;
  createTime: string;
  status: TenantStatus;
}

export interface TenantStoreState {
  tenants: Tenant[];
  currentTenant: Tenant | null;
  disableModalVisible: boolean;
  isLoading: boolean;
}

export interface TenantStoreActions {
  fetchTenants: () => Promise<void>;
  createTenant: (tenant: Omit<Tenant, 'id' | 'createTime'>) => Promise<void>;
  updateTenant: (id: string, updates: Partial<Tenant>) => Promise<void>;
  disableTenant: (id: string) => Promise<void>;
  setCurrentTenant: (tenant: Tenant | null) => void;
  showDisableModal: (visible: boolean) => void;
  reset: () => void;
}