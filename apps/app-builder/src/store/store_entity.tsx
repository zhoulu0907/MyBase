// TODO(mickey): remove
// import type { AppEntities, AppEntity } from '@onebase/app';
// import { create } from 'zustand';

// export interface appEntityStore {
//   mainEntity: AppEntity;
//   appEntities: AppEntities;
//   setAppEntities: (appEntities: AppEntities) => void;
//   clearAppEntities: () => void;

//   setMainEntity: (mainEntity: AppEntity) => void;
//   clearMainEntity: () => void;

//   subEntities: AppEntities;
//   setSubEntities: (subEntities: AppEntities) => void;
//   clearSubEntities: () => void;
// }

// export const useAppEntityStore = create<appEntityStore>((set) => ({
//   appEntities: {
//     entities: []
//   } as AppEntities,
//   setAppEntities: (appEntities: AppEntities) => set(() => ({ appEntities })),
//   clearAppEntities: () =>
//     set(() => ({
//       appEntities: {
//         entities: []
//       } as AppEntities
//     })),

//   mainEntity: {
//     entityId: '',
//     entityName: '',
//     entityType: '',
//     fields: []
//   } as AppEntity,
//   setMainEntity: (mainEntity: AppEntity) => set(() => ({ mainEntity })),
//   clearMainEntity: () => set(() => ({ mainEntity: { entityId: '', entityName: '', entityType: '', fields: [] } })),

//   subEntities: {
//     entities: []
//   } as AppEntities,
//   setSubEntities: (subEntities: AppEntities) => set(() => ({ subEntities })),
//   clearSubEntities: () => set(() => ({ subEntities: { entities: [] } }))
// }));
