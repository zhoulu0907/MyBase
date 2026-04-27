import { signal } from '@preact/signals-react';
import { ApplicationMenu } from '../types';

export const createMenuSignal = () => {
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
export const menuSignal = createMenuSignal();
