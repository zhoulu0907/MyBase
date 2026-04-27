import { create } from 'zustand';
import type { EditConfig } from '../components/Materials/types';

export interface BasicEditorStore {
  // 是否是编辑模式
  isEditMode: boolean;
  // 设置是否是编辑模式
  setIsEditMode: (isEditMode: boolean) => void;
  // 清除是否是编辑模式
  clearIsEditMode: () => void;

  // 当前选中的组件ID
  curComponentID: string;
  // 设置当前选中的组件ID
  setCurComponentID: (cp_id: string) => void;
  // 清除当前选中的组件ID
  clearCurComponentID: () => void;

  // 当前选中的组件配置
  curComponentSchema: EditConfig;
  // 设置当前选中的组件配置
  setCurComponentSchema: (config: EditConfig) => void;

  // 是否显示删除按钮
  showDeleteButton: boolean;
  // 设置是否显示删除按钮
  setShowDeleteButton: (show: boolean) => void;
}

export const useBasicEditorStore = create<BasicEditorStore>((set) => ({
  isEditMode: false,

  setIsEditMode: (isEditMode: boolean) => set(() => ({ isEditMode })),
  clearIsEditMode: () => set(() => ({ isEditMode: false })),

  curComponentID: '',
  setCurComponentID: (cp_id: string) => set(() => ({ curComponentID: cp_id })),
  clearCurComponentID: () => set(() => ({ curComponentID: '' })),

  curComponentSchema: {},
  setCurComponentSchema: (config: EditConfig) => set(() => ({ curComponentSchema: config })),

  showDeleteButton: false,
  setShowDeleteButton: (show: boolean) => set(() => ({ showDeleteButton: show }))
}));
