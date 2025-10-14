import { signal } from '@preact/signals-react';

export const createPagesRuntimeSignal = () => {
  // page列表
  const curPage = signal<any>({});

  const setCurPage = (newPage: any) => {
    curPage.value = newPage;
  };

  //   抽屉
  const drawerVisible = signal<boolean>(false);
  const setDrawerVisible = (newDrawerVisible: boolean) => {
    drawerVisible.value = newDrawerVisible;
  };
  const drawerPageId = signal<string>('');
  const setDrawerPageId = (newDrawerPageId: string) => {
    drawerPageId.value = newDrawerPageId;
  };

  return {
    curPage,
    setCurPage,

    drawerVisible,
    setDrawerVisible,

    drawerPageId,
    setDrawerPageId
  };
};

// 创建默认的 store 实例（向后兼容）
export const pagesRuntimeSignal = createPagesRuntimeSignal();
