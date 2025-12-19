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
  const bpmInstanceId = signal<string>('');
  const setBpmInstanceId = (newBpmInstanceId: string) => {
    bpmInstanceId.value = newBpmInstanceId;
  };
  const editPageViewId = signal<string>('');
  const setEditPageViewId = (newEditPageViewId: string) => {
    editPageViewId.value = newEditPageViewId;
  };
  const detailPageViewId = signal<string>('');
  const setDetailPageViewId = (newDetailPageViewId: string) => {
    detailPageViewId.value = newDetailPageViewId;
  };
  // 表格行 数据id
  const rowDataId = signal<string>('');
  const setRowDataId = (newEntityDataId: string) => {
    rowDataId.value = newEntityDataId;
  };

  const mainMetaDataFields = signal<any[]>([]);
  const setMainMetaDataFields = (fields: any[]) => {
    mainMetaDataFields.value = fields;
  };

  const subTableDataLength = signal<Record<string, number>>({});
  const setSubTableDataLength = (subTableId: string, length: number) => {
    console.log('subTableId: ', subTableId, 'length: ', length);
    subTableDataLength.value = { ...subTableDataLength.value, [subTableId]: length };
  };
  const resetSubTableDataLength = () => {
    subTableDataLength.value = {};
  };

  const subEntities = signal<any[]>([]);
  const setSubEntities = (entities: any[]) => {
    subEntities.value = entities;
  };

  return {
    curPage,
    setCurPage,

    drawerVisible,
    setDrawerVisible,

    drawerPageId,
    setDrawerPageId,

    bpmInstanceId,
    setBpmInstanceId,

    editPageViewId,
    setEditPageViewId,

    detailPageViewId,
    setDetailPageViewId,

    rowDataId,
    setRowDataId,

    mainMetaDataFields,
    setMainMetaDataFields,

    subTableDataLength,
    setSubTableDataLength,
    resetSubTableDataLength,

    subEntities,
    setSubEntities
  };
};

// 创建默认的 store 实例（向后兼容）
export const pagesRuntimeSignal = createPagesRuntimeSignal();
