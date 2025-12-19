import { signal } from '@preact/signals-react';
import type { EditConfig } from '../components/Materials/types';

const normalizeIds = (ids: Set<string> | string[]) => {
  return ids instanceof Set ? ids : new Set(ids);
};

/**
 * 工作台编辑器Signal
 */
export const createWorkbenchEditorSignal = () => {
  // Signal 1: 工作台组件列表
  const workbenchComponents = signal<any[]>([]);

  const setWorkbenchComponents = (components: any[]) => {
    workbenchComponents.value = components;
  };

  const loadWorkbenchComponents = (components: any[]) => {
    workbenchComponents.value = components;
  };

  const addWorkbenchComponent = (component: any) => {
    workbenchComponents.value = [...workbenchComponents.value, component];
  };

  const delWorkbenchComponents = (cpId: string) => {
    workbenchComponents.value = workbenchComponents.value.filter(
      (component) => component.id !== cpId
    );
  };

  const batchDelWorkbenchComponents = (ids: Set<string> | string[]) => {
    const idSet = normalizeIds(ids);
    workbenchComponents.value = workbenchComponents.value.filter(
      (component) => !idSet.has(component.id)
    );
  };

  const clearWorkbenchComponents = () => {
    workbenchComponents.value = [];
  };

  // Signal 2: 工作台组件配置Schema
  const wbComponentSchemas = signal<{ [key: string]: EditConfig }>({});

  const setWbComponentSchemas = (cp_id: string, config: EditConfig) => {
    wbComponentSchemas.value = { ...wbComponentSchemas.value, [cp_id]: config };
  };

  const loadWbComponentSchemas = (config: { [key: string]: EditConfig }) => {
    wbComponentSchemas.value = config;
  };

  const delWbComponentSchemas = (cp_id: string) => {
    const newSchemas = { ...wbComponentSchemas.value };
    delete newSchemas[cp_id];
    wbComponentSchemas.value = newSchemas;
  };

  const batchDelWbComponentSchemas = (ids: Set<string> | string[]) => {
    const newSchemas = { ...wbComponentSchemas.value };
    const idArray = ids instanceof Set ? Array.from(ids) : ids;
    idArray.forEach((id: string) => {
      if (newSchemas[id]) {
        delete newSchemas[id];
      }
    });
    wbComponentSchemas.value = newSchemas;
  };

  const clearWbComponentSchemas = () => {
    wbComponentSchemas.value = {};
  };

  return {
    // 工作台组件
    workbenchComponents,
    setWorkbenchComponents,
    loadWorkbenchComponents,
    addWorkbenchComponent,
    delWorkbenchComponents,
    batchDelWorkbenchComponents,
    clearWorkbenchComponents,

    // 组件配置Schema
    wbComponentSchemas,
    setWbComponentSchemas,
    loadWbComponentSchemas,
    delWbComponentSchemas,
    batchDelWbComponentSchemas,
    clearWbComponentSchemas
  };
};

// 导出类型
export type WorkbenchEditorSignal = ReturnType<typeof createWorkbenchEditorSignal>;

// 单例实例
export const useWorkbenchEditorSignal = createWorkbenchEditorSignal();

// 类型守卫
export const isWorkbenchEditorSignal = (
  signal: unknown
): signal is WorkbenchEditorSignal => {
  return !!signal && typeof (signal as WorkbenchEditorSignal).setWorkbenchComponents === 'function';
};
