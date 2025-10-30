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

  const loadPageComponentSchemas = (config: { [key: string]: EditConfig }) => {
    pageComponentSchemas.value = config;
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

  const loadLayoutSubComponents = (config: { [key: string]: any[][] }) => {
    layoutSubComponents.value = config;
  };

  const delLayoutSubComponents = (cp_id: string) => {
    const newLayoutSubComponents = { ...layoutSubComponents.value };
    delete newLayoutSubComponents[cp_id];
    layoutSubComponents.value = newLayoutSubComponents;
  };

  const clearLayoutSubComponents = () => {
    layoutSubComponents.value = {};
  };

   // 子表单
  const subTableComponents = signal<{ [key: string]: any[] }>({});
  const setSubTableComponents = (cp_id: string, newColumns: any[]) => {
    subTableComponents.value = { ...subTableComponents.value, [cp_id]: newColumns };
  };
  const loadSubTableComponents = (config: { [key: string]: any[] }) => {
    subTableComponents.value = config;
  };
  const delSubTableComponents = (cp_id: string) => {
    const newSubTableComponents = { ...subTableComponents.value };
    delete newSubTableComponents[cp_id];
    subTableComponents.value = newSubTableComponents;
  };
  const clearSubTableComponents = () => {
    subTableComponents.value = {};
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
    loadPageComponentSchemas,
    delPageComponentSchemas,
    clearPageComponentSchemas,

    // 列布局组件的列数据
    layoutSubComponents,
    loadLayoutSubComponents,
    setLayoutSubComponents,
    delLayoutSubComponents,
    clearLayoutSubComponents,

    // 子表单组件的列数据
    subTableComponents,
    loadSubTableComponents,
    setSubTableComponents,
    delSubTableComponents,
    clearSubTableComponents,

  };
};

export const useFormEditorSignal = createPageEditorSignal();
export const useListEditorSignal = createPageEditorSignal();

export const createEditorSignalMap = () => {
  const editorSignalMap = signal<Map<string, ReturnType<typeof createPageEditorSignal>>>(new Map());

  const set = (pageId: string, editorSignal: ReturnType<typeof createPageEditorSignal>) => {
    editorSignalMap.value.set(pageId, editorSignal);
  };

  const get = (pageId: string) => {
    return editorSignalMap.value.get(pageId);
  };

  return {
    editorSignalMap,
    set,
    get
  };
};

export const useEditorSignalMap = createEditorSignalMap();
