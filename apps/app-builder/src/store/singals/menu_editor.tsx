import type { ApplicationMenu } from '@onebase/app';
import { signal } from '@preact/signals-react';

export const createMenuEditorSignal = () => {
  const curMenu = signal<ApplicationMenu>({} as ApplicationMenu);
  const setCurMenu = (menu: ApplicationMenu) => {
    curMenu.value = menu;
  };

  return {
    curMenu,
    setCurMenu
  };
};

// 创建默认的 store 实例（向后兼容）
export const menuEditorSignal = createMenuEditorSignal();
