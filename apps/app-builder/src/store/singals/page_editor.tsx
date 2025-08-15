import type { EditConfig } from '@/components/Materials/types';
import { signal } from '@preact/signals-react';

// 创建编辑器组件管理 store 的工厂函数
export const createPageEditorSignal = (initialComponents: EditConfig[] = []) => {
  const components = signal(initialComponents);

  const setComponents = (newComponents: any[]) => {
    console.log('setComponents', newComponents);
    components.value = newComponents;
  };

  const addComponent = (component: any) => {
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

  //   const colComponents = signal<{ [key: string]: any[][] }>({});

  //   const setColComponents = (cp_id: string, newColumns: any[][]) => {
  //     colComponents.value = { ...colComponents.value, [cp_id]: newColumns };
  //   };

  //   const delColComponents = (cp_id: string) => {
  //     const newMap = { ...colComponents.value };
  //     delete newMap[cp_id];
  //     colComponents.value = newMap;
  //   };

  //   const clearColComponents = () => {
  //     colComponents.value = {};
  //   };

  return {
    // 页面组件
    components,
    setComponents,
    addComponent,
    delComponents,
    clearComponents,

    // 页面组件配置
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    clearPageComponentSchemas

    // // 列布局组件的列数据
    // colComponents,
    // setColComponents,
    // delColComponents,
    // clearColComponents
  };
};

// 创建默认的 store 实例（向后兼容）

export const useFormEditorSignal = createPageEditorSignal();
export const useListEditorSignal = createPageEditorSignal();
