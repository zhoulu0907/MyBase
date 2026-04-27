import { create } from 'zustand';

export interface resourceStore {
  // 当前数据源ID
  curDataSourceId: string;
  // 设置当前数据源ID
  setCurDataSourceId: (dataSourceId: string) => void;
  // 清除当前数据源ID
  clearCurDataSourceId: () => void;
}

export const useResourceStore = create<resourceStore>((set) => ({
  curDataSourceId: '',
  setCurDataSourceId: (dataSourceId: string) => set(() => ({ curDataSourceId: dataSourceId })),
  clearCurDataSourceId: () => set(() => ({ curDataSourceId: '' }))
}));
