import { create } from 'zustand';
import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';

export interface AppEntitiesStore {
  // 当前应用下的所有实体列表
  appEntities: EntityListItem[];
  // 设置实体列表
  setAppEntities: (entities: EntityListItem[]) => void;
  // 清除实体列表
  clearAppEntities: () => void;
}

export const useAppEntitiesStore = create<AppEntitiesStore>((set) => ({
  appEntities: [],
  setAppEntities: (appEntities: EntityListItem[]) => set(() => ({ appEntities })),
  clearAppEntities: () => set(() => ({ appEntities: [] }))
}));
