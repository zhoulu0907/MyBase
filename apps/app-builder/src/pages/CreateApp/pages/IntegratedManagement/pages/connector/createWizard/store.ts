import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { getConnectorTypeInfo } from '@onebase/app';

interface ConnectorWizardState {
  // 当前步骤 (0-4)
  currentStep: number;

  // 连接器类型信息
  connectorType: {
    nodeCode: string;
    nodeName: string;
    version: string;
  };

  // 动态 schemas（从后端获取）
  schemas: {
    conn_config: any;
    action_config: any;
  };

  // 动态表单数据
  formData: {
    basicInfo: {
      connectorName: string;
      description: string;
    };
    conn_config: Record<string, any>;
    action_config: Record<string, any>;
    relatedFlows: any[];
    requestLogs: any[];
  };

  // 环境列表（用于"选择已有环境信息"模式）
  envList: Array<{
    id: string;
    name: string;
    url?: string;
  }>;

  // UI 状态
  ui: {
    isLoading: boolean;
    error?: string;
  };
}

interface ConnectorWizardActions {
  setCurrentStep: (step: number) => void;
  nextStep: () => void;
  prevStep: () => void;
  updateConnectorType: (type: ConnectorWizardState['connectorType']) => void;
  updateFormData: (data: Partial<ConnectorWizardState['formData']>) => void;
  fetchSchemas: (nodeCode: string) => Promise<void>;
  fetchEnvList: () => Promise<void>;
  reset: () => void;
}

type ConnectorWizardStore = ConnectorWizardState & ConnectorWizardActions;

export const useConnectorWizardStore = create<ConnectorWizardStore>()(
  persist(
    (set, get) => ({
      // 初始状态
      currentStep: 0,
      connectorType: {
        nodeCode: '',
        nodeName: '',
        version: '',
      },
      schemas: {
        conn_config: null,
        action_config: null,
      },
      formData: {
        basicInfo: {
          connectorName: '',
          description: '',
        },
        conn_config: {},
        action_config: {},
        relatedFlows: [],
        requestLogs: [],
      },
      ui: {
        isLoading: false,
      },

      envList: [],

      // Actions
      setCurrentStep: (step) => set({ currentStep: step }),

      nextStep: () => set((state) => ({ currentStep: Math.min(state.currentStep + 1, 4) })),

      prevStep: () => set((state) => ({ currentStep: Math.max(state.currentStep - 1, 0) })),

      updateConnectorType: (type) => set({ connectorType: type }),

      updateFormData: (data) =>
        set((state) => ({
          formData: { ...state.formData, ...data },
        })),

      fetchSchemas: async (nodeCode) => {
        set({ ui: { ...get().ui, isLoading: true } });
        try {
          const res = await getConnectorTypeInfo(nodeCode);
          if (res) {
            set({
              schemas: {
                conn_config: res.conn_config || null,
                action_config: res.action_config || null,
              },
              ui: { isLoading: false },
            });
          }
        } catch (error) {
          console.error('获取 schema 失败:', error);
          set({ ui: { ...get().ui, isLoading: false, error: '获取配置失败' } });
        }
      },

      fetchEnvList: async () => {
        try {
          // TODO: 等待后端 API 接口确认后实现
          // 预计接口: GET /env/list
          // 临时返回空数组，功能待实现
          set({ envList: [] });
        } catch (error) {
          console.error('获取环境列表失败:', error);
          set({ envList: [] });
        }
      },

      reset: () =>
        set({
          currentStep: 0,
          connectorType: { nodeCode: '', nodeName: '', version: '' },
          schemas: { conn_config: null, action_config: null },
          formData: {
            basicInfo: { connectorName: '', description: '' },
            conn_config: {},
            action_config: {},
            relatedFlows: [],
            requestLogs: [],
          },
          ui: { isLoading: false, error: undefined },
          envList: [],
        }),
    }),
    {
      name: 'connector-wizard-storage',
      partialize: (state) => ({
        currentStep: state.currentStep,
        connectorType: state.connectorType,
        schemas: state.schemas,
        formData: state.formData,
        envList: state.envList,
      }),
    }
  )
);
