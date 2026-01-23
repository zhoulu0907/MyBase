import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { getConnectorTypeInfo, getEnvList } from '@onebase/app';

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
        console.log('[fetchSchemas] 开始获取 schema，nodeCode:', nodeCode);
        set({ ui: { ...get().ui, isLoading: true } });
        try {
          const res = await getConnectorTypeInfo(nodeCode);
          console.log('[fetchSchemas] 后端返回完整响应:', res);

          // 检查后端是否返回了有效的 schema
          const hasValidConnConfig = res?.conn_config && Object.keys(res.conn_config).length > 0;
          const hasValidActionConfig = res?.action_config && Object.keys(res.action_config).length > 0;

          console.log('[fetchSchemas] Schema 有效性检查:', {
            hasValidConnConfig,
            hasValidActionConfig,
            connConfigKeys: res?.conn_config ? Object.keys(res.conn_config) : [],
            actionConfigKeys: res?.action_config ? Object.keys(res.action_config) : []
          });

          if (hasValidConnConfig || hasValidActionConfig) {
            // 后端返回了有效的 schema，使用后端数据
            console.log('[fetchSchemas] 使用后端返回的 schema');
            set({
              schemas: {
                conn_config: res.conn_config || null,
                action_config: res.action_config || null,
              },
              ui: { isLoading: false },
            });
          } else {
            // 后端未返回有效 schema，使用 mock schema 用于开发测试
            console.warn('[fetchSchemas] 后端未返回有效 schema，使用临时 mock schema');
            const mockConnConfig = {
              type: "object",
              properties: {
                envMode: {
                  type: "string",
                  title: "环境信息",
                  enum: ["create", "select"],
                  enumNames: ["创建环境信息", "选择已有环境信息"],
                  default: "create",
                  "x-decorator": "FormItem",
                  "x-component": "Radio.Group",
                  "x-component-props": {
                    optionType: "button",
                    buttonStyle: "solid"
                  }
                },
                existingEnvId: {
                  type: "string",
                  title: "请选择环境信息",
                  "x-decorator": "FormItem",
                  "x-component": "Select",
                  "x-visible": "{{ $form.values.envMode === 'select' }}",
                  "x-component-props": {
                    placeholder: "请选择环境信息",
                    allowClear: true,
                    disabled: true // 暂时禁用，后端 API 就绪后启用
                  }
                },
                envName: {
                  type: "string",
                  title: "环境名称",
                  "x-decorator": "FormItem",
                  "x-component": "Input",
                  "x-visible": "{{ $form.values.envMode === 'create' }}",
                  "x-component-props": {
                    placeholder: "请输入环境名称"
                  },
                  "x-reactions": [
                    {
                      dependencies: ["envMode"],
                      fulfill: {
                        state: {
                          visible: "{{$deps[0] === 'create'}}"
                        }
                      }
                    }
                  ],
                  required: true
                },
                url: {
                  type: "string",
                  title: "URL",
                  "x-decorator": "FormItem",
                  "x-component": "Input",
                  "x-visible": "{{ $form.values.envMode === 'create' }}",
                  "x-component-props": {
                    placeholder: "请输入URL"
                  },
                  "x-reactions": [
                    {
                      dependencies: ["envMode"],
                      fulfill: {
                        state: {
                          visible: "{{$deps[0] === 'create'}}"
                        }
                      }
                    }
                  ],
                  required: true
                },
                authType: {
                  type: "string",
                  title: "选择认证类型",
                  "x-decorator": "FormItem",
                  "x-component": "Select",
                  "x-visible": "{{ $form.values.envMode === 'create' }}",
                  "x-component-props": {
                    placeholder: "请选择认证类型",
                    allowClear: false
                  },
                  "x-reactions": [
                    {
                      dependencies: ["envMode"],
                      fulfill: {
                        state: {
                          visible: "{{$deps[0] === 'create'}}"
                        }
                      }
                    }
                  ],
                  "default": "none",
                  "enum": ["none", "basic", "bearer", "apiKey", "oauth2", "custom", "awsSignature"],
                  "enumNames": ["无认证", "Basic认证", "Bearer认证", "API Key", "OAuth 2.0", "自定义认证", "AWS签名"],
                  required: true
                }
              }
            };

            const schemasToSet = {
              conn_config: mockConnConfig,
              action_config: null,
            };
            console.log('[fetchSchemas] 设置 mock schema:', schemasToSet);
            set({
              schemas: schemasToSet,
              ui: { isLoading: false },
            });
            console.log('[fetchSchemas] schema 设置完成，当前 store schemas:', get().schemas);
          }
        } catch (error) {
          console.error('[fetchSchemas] 获取 schema 失败:', error);
          set({ ui: { ...get().ui, isLoading: false, error: '获取配置失败' } });
        }
      },

      fetchEnvList: async () => {
        try {
          // TODO: 后端 API 开发完成后，启用下面的代码
          // const res = await getEnvList();
          // if (Array.isArray(res)) {
          //   set({ envList: res });
          // } else if (res?.list && Array.isArray(res.list)) {
          //   set({ envList: res.list });
          // } else {
          //   set({ envList: [] });
          // }

          // MOCK 数据 - 后端 API 开发前使用
          // 模拟延迟，让加载状态可见
          await new Promise(resolve => setTimeout(resolve, 500));

          const mockEnvList = [
            { id: 'env-001', name: '测试环境', url: 'https://test.example.com' },
            { id: 'env-002', name: '开发环境', url: 'https://dev.example.com' },
            { id: 'env-003', name: '预发布环境', url: 'https://staging.example.com' },
            { id: 'env-004', name: '生产环境', url: 'https://prod.example.com' },
          ];

          set({ envList: mockEnvList });
          console.log('使用 MOCK 环境列表数据:', mockEnvList);
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
