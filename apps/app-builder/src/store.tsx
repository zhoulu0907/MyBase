import { create } from 'zustand';
import type { EditConfig } from './components/Materials/types';

export interface BasicEditorStore {
  // 当前选中的组件ID
  curComponentID: string;
  // 设置当前选中的组件ID
  setCurComponentID: (cp_id: string) => void;
  // 清除当前选中的组件ID
  clearCurComponentID: () => void;

  // 当前选中的组件配置
  curComponentSchema: EditConfig;
  // 设置当前选中的组件配置
  setCurComponentSchema: (config: EditConfig) => void;

  // 是否显示删除按钮
  showDeleteButton: boolean;
  // 设置是否显示删除按钮
  setShowDeleteButton: (show: boolean) => void;
}

export interface PageEditorStore {
  // 表单设计组件
  components: any[];
  setComponents: (components: any[]) => void;
  delComponents: (cp_id: string) => void;
  clearComponents: () => void;

  // 页面组件配置(key: 组件ID, value: 组件配置)
  pageComponentSchemas: Map<string, EditConfig>;
  // 设置页面组件配置
  setPageComponentSchemas: (cp_id: string, config: EditConfig) => void;
  // 删除页面组件配置
  delPageComponentSchemas: (cp_id: string) => void;
  // 清空页面组件配置
  clearPageComponentSchemas: () => void;

  // 列布局组件的列数据 (key: 组件ID, value: 二维数组，每个子数组代表一列的组件集合)
  colComponentsMap: {
    colComponents: Map<string, any[][]>;
  };
  // 设置列布局组件的列数据
  setColComponentsMap: (cp_id: string, columnsOrUpdater: any[][] | ((prevColumns: any[][]) => any[][])) => void;
  // 删除列布局组件的列数据
  delColComponentsMap: (cp_id: string) => void;
  // 清空列布局组件的列数据
  clearColComponentsMap: () => void;
}

// 创建页面编辑器 store 的工厂函数
const createPageEditorStore = () =>
  create<PageEditorStore>((set) => ({
    components: [],
    setComponents: (components: any[]) => set(() => ({ components })),
    delComponents: (cp_id: string) =>
      set((state) => {
        const newComponents = state.components.filter((component) => component.id !== cp_id);
        return { components: newComponents };
      }),
    clearComponents: () => set(() => ({ components: [] })),

    pageComponentSchemas: new Map(),
    setPageComponentSchemas: (cp_id: string, config: EditConfig) =>
      set((state) => {
        const newMap = new Map(state.pageComponentSchemas);
        newMap.set(cp_id, config);
        return { pageComponentSchemas: newMap };
      }),
    delPageComponentSchemas: (cp_id: string) =>
      set((state) => {
        const newMap = new Map(state.pageComponentSchemas);
        newMap.delete(cp_id);
        return { pageComponentSchemas: newMap };
      }),
    clearPageComponentSchemas: () => set(() => ({ pageComponentSchemas: new Map() })),

    colComponentsMap: {
      colComponents: new Map()
    },
    setColComponentsMap: (cp_id: string, columnsOrUpdater: any[][] | ((prevColumns: any[][]) => any[][])) =>
      set((state) => {
        const newMap = new Map(state.colComponentsMap.colComponents);
        const currentColumns = newMap.get(cp_id) || [];

        let newColumns: any[][];
        if (typeof columnsOrUpdater === 'function') {
          newColumns = columnsOrUpdater(currentColumns);
        } else {
          newColumns = columnsOrUpdater;
        }

        newMap.set(cp_id, newColumns);
        return { colComponentsMap: { colComponents: newMap } };
      }),
    delColComponentsMap: (cp_id: string) =>
      set((state) => {
        const newMap = new Map(state.colComponentsMap.colComponents);
        newMap.delete(cp_id);
        return { colComponentsMap: { colComponents: newMap } };
      }),
    clearColComponentsMap: () => set(() => ({ colComponentsMap: { colComponents: new Map() } }))
  }));

export const useBasicEditorStore = create<BasicEditorStore>((set) => ({
  curComponentID: '',
  setCurComponentID: (cp_id: string) => set(() => ({ curComponentID: cp_id })),
  clearCurComponentID: () => set(() => ({ curComponentID: '' })),

  curComponentSchema: {},
  setCurComponentSchema: (config: EditConfig) => set(() => ({ curComponentSchema: config })),

  showDeleteButton: false,
  setShowDeleteButton: (show: boolean) => set(() => ({ showDeleteButton: show }))
}));

// 使用工厂函数创建两个独立的 store 实例
export const useFromEditorStore = createPageEditorStore();
export const useListEditorStore = createPageEditorStore();

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
