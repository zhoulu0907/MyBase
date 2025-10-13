import { signal } from '@preact/signals-react';

export const createMenuEditorSignal = () => {
  // 节点列表
  const curMenuId = signal<string>('');
  const setCurMenuId = (menu_id: string) => {
    curMenuId.value = menu_id;
  };

  return {
    curMenuId,
    setCurMenuId
  };
};

// 创建默认的 store 实例（向后兼容）
export const menuEditorSignal = createMenuEditorSignal();
