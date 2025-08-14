import type { AppEntities, AppEntity } from '@onebase/app';
import { create } from 'zustand';

export interface appEntityStore {
  mainEntity: AppEntity;
  appEntities: AppEntities;
  setAppEntities: (appEntities: AppEntities) => void;
  clearAppEntities: () => void;

  setMainEntity: (mainEntity: AppEntity) => void;
  clearMainEntity: () => void;
}

export const useAppEntityStore = create<appEntityStore>((set) => ({
  appEntities: {
    entities: []
  },
  setAppEntities: (appEntities: AppEntities) => set(() => ({ appEntities })),
  clearAppEntities: () =>
    set(() => ({
      appEntities: {
        entities: []
      }
    })),

  mainEntity: {
    entityID: '',
    entityName: '',
    entityType: '',
    fields: []
  },
  setMainEntity: (mainEntity: AppEntity) => set(() => ({ mainEntity })),
  clearMainEntity: () => set(() => ({ mainEntity: { entityID: '', entityName: '', entityType: '', fields: [] } }))
}));
