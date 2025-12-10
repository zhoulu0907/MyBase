// import { signal } from '@preact/signals-react';
import { createPageEditorSignal } from './page_editor';

const normalizeIds = (ids: Set<string> | string[]) => {
  return ids instanceof Set ? ids : new Set(ids);
};

export const createWorkbenchEditorSignal = () => {
  const baseSignal = createPageEditorSignal();
  const workbenchComponents = baseSignal.components;

  // 后续看是否要独立维护一个 signal
  // const workbenchComponents = signal<any[]>([]);

  const setWorkbenchComponents = (components: any[]) => {
    // workbenchComponents.value = components;

    baseSignal.setComponents(components);
  };

  const loadWorkbenchComponents = (components: any[]) => {
    // workbenchComponents.value = components;

    baseSignal.setComponents(components);
  };

  const delWorkbenchComponents = (cpId: string) => {
    // workbenchComponents.value = workbenchComponents.value.filter((component) => component.id !== cpId);

    baseSignal.delComponents(cpId);
  };  

  const batchDelWorkbenchComponents = (ids: Set<string> | string[]) => {
    const idSet = normalizeIds(ids);
    workbenchComponents.value = workbenchComponents.value.filter((component) => !idSet.has(component.id));
  };

  const clearWorkbenchComponents = () => {
    // workbenchComponents.value = [];

    baseSignal.clearComponents();
  };

  return {
    ...baseSignal,
    workbenchComponents,
    setWorkbenchComponents,
    loadWorkbenchComponents,
    delWorkbenchComponents,
    batchDelWorkbenchComponents,
    clearWorkbenchComponents
  };
};

export type WorkbenchEditorSignal = ReturnType<typeof createWorkbenchEditorSignal>;

export const useWorkbenchEditorSignal = createWorkbenchEditorSignal();

export const isWorkbenchEditorSignal = (
  signal: unknown
): signal is WorkbenchEditorSignal => {
  return !!signal && typeof (signal as WorkbenchEditorSignal).setWorkbenchComponents === 'function';
};

