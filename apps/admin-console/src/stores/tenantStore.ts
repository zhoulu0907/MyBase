import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import type { Tenant, TenantStoreState, TenantStoreActions } from '@/types/tenant';

// 合并状态和动作类型
export type TenantStore = TenantStoreState & TenantStoreActions;

// 初始状态
const initialState: TenantStoreState = {
  tenants: [],
  currentTenant: null,
  disableModalVisible: false,
  isLoading: false
};

// 模拟API调用
const mockTenants: Tenant[] = [
  {
    id: '1',
    name: '默认用户',
    code: 'ZH2025070001',
    allocatedCount: 50,
    admin: '石头',
    createTime: '2025-08-14T10:30:00Z',
    status: 'enabled'
  },
  {
    id: '2',
    name: '测试环境验证用户',
    code: 'ZH2025070002',
    allocatedCount: 50,
    admin: '石头',
    createTime: '2025-08-14T10:30:00Z',
    status: 'disabled'
  }
];

// 模拟API延迟
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const useTenantStore = create<TenantStore>()(
  devtools(
    (set, get) => ({
      // 初始状态
      ...initialState,

      // 动作
      fetchTenants: async () => {
        set({ isLoading: true });
        try {
          // 模拟API调用
          await delay(500);
          set({ tenants: mockTenants, isLoading: false });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      // ... existing code ...

      createTenant: async (tenantData) => {
        // 模拟API调用
        await delay(300);
        const newTenant: Tenant = {
          id: String(get().tenants.length + 1),
          ...tenantData,
          createTime: new Date().toISOString()
        };

        set((state) => ({
          tenants: [...state.tenants, newTenant]
        }));
      },

      updateTenant: async (id, updates) => {
        // 模拟API调用
        await delay(300);

        set((state) => ({
          tenants: state.tenants.map((tenant) => (tenant.id === id ? { ...tenant, ...updates } : tenant))
        }));
      },

      disableTenant: async (id) => {
        // 模拟API调用
        await delay(300);

        set((state) => ({
          tenants: state.tenants.map((tenant) => (tenant.id === id ? { ...tenant, status: 'disabled' } : tenant))
        }));
      },

      setCurrentTenant: (tenant) => {
        set({ currentTenant: tenant });
      },

      showDisableModal: (visible) => {
        set({ disableModalVisible: visible });
      },

      reset: () => {
        set(initialState);
      }
    }),
    { name: 'TenantStore' }
  )
);
