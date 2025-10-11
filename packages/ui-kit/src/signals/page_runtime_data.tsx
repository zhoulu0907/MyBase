import { signal } from '@preact/signals-react';

export const createPagesRuntimeDataSignal = () => {
  // page列表
  const curPage = signal<any>({});
  
  const setCurPage = (newPage: any) => {
    curPage.value = newPage;
  };

  return {
    curPage,
    setCurPage,
  };
};

// 创建默认的 store 实例（向后兼容）
export const pagesRuntimeDataSignal = createPagesRuntimeDataSignal();