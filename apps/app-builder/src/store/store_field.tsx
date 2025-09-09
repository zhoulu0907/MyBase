import { create } from 'zustand';

interface FieldType {
  displayName: string;
  fieldType: string;
}

export interface fieldStore {
  // 当前应用的字段类型
  fieldTypes: Array<FieldType>;
  // 设置当前应用的字段类型
  setFieldTypes: (fieldTypes: Array<FieldType>) => void;
  // 清除当前应用的字段类型
  clearFieldTypes: () => void;
}

export const useFieldStore = create<fieldStore>((set) => ({
  fieldTypes: [],
  setFieldTypes: (fieldTypes: Array<FieldType>) => set(() => ({ fieldTypes })),
  clearFieldTypes: () => set(() => ({ fieldTypes: [] }))
}));
