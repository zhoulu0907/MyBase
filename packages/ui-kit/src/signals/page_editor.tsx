import { signal } from '@preact/signals-react';
import type { EditConfig } from '../components/Materials/types';

// 创建编辑器组件管理 store 的工厂函数
export const createPageEditorSignal = (initialComponents: EditConfig[] = []) => {
  const components = signal(initialComponents);

  const setComponents = (newComponents: any[]) => {
    components.value = newComponents;
  };

  const addComponents = (component: any) => {
    components.value = [...components.value, component];
  };

  const delComponents = (cp_id: string) => {
    components.value = components.value.filter((component) => component.id !== cp_id);
  };

  const clearComponents = () => {
    components.value = [];
  };

  const pageComponentSchemas = signal<{ [key: string]: EditConfig }>({});

  const setPageComponentSchemas = (cp_id: string, config: EditConfig) => {
    pageComponentSchemas.value = { ...pageComponentSchemas.value, [cp_id]: config };
  };

  const delPageComponentSchemas = (cp_id: string) => {
    const newSchemas = { ...pageComponentSchemas.value };
    delete newSchemas[cp_id];
    pageComponentSchemas.value = newSchemas;
  };

  const clearPageComponentSchemas = () => {
    pageComponentSchemas.value = {};
  };

  const layoutSubComponents = signal<{ [key: string]: any[][] }>({});

  const setLayoutSubComponents = (cp_id: string, newColumns: any[][]) => {
    layoutSubComponents.value = { ...layoutSubComponents.value, [cp_id]: newColumns };
  };

  const delLayoutSubComponents = (cp_id: string) => {
    const newLayoutSubComponents = { ...layoutSubComponents.value };
    delete newLayoutSubComponents[cp_id];
    layoutSubComponents.value = newLayoutSubComponents;
  };

  const clearLayoutSubComponents = () => {
    layoutSubComponents.value = {};
  };

  return {
    // 页面组件
    components,
    setComponents,
    addComponents,
    delComponents,
    clearComponents,

    // 页面组件配置
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    clearPageComponentSchemas,

    // 列布局组件的列数据
    layoutSubComponents,
    setLayoutSubComponents,
    delLayoutSubComponents,
    clearLayoutSubComponents
  };
};

export const useFormEditorSignal = createPageEditorSignal();
export const useListEditorSignal = createPageEditorSignal();
