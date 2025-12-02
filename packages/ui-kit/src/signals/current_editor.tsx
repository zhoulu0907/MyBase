import { signal } from '@preact/signals-react';
import type { EditConfig } from '../components/Materials/types';

// 创建编辑器组件管理 store 的工厂函数
export const createCurrentEditorSignal = () => {
  // 编辑模式信号
  const editMode = signal<string>('');

  const setEditMode = (em: string) => {
    editMode.value = em;
  };

  const clearEditMode = () => {
    editMode.value = '';
  };

  // 当前选中组件ID信号
  const curComponentID = signal<string>('');

  const setCurComponentID = (cp_id: string) => {
    curComponentID.value = cp_id;
  };

  const clearCurComponentID = () => {
    curComponentID.value = '';
  };

  // 当前选中组件配置信号
  const curComponentSchema = signal<EditConfig>({});

  const setCurComponentSchema = (config: EditConfig) => {
    curComponentSchema.value = config;
  };

  // 是否显示删除按钮信号
  const showDeleteButton = signal(false);

  const setShowDeleteButton = (show: boolean) => {
    showDeleteButton.value = show;
  };

  return {
    editMode,
    setEditMode,
    clearEditMode,
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema,
    setCurComponentSchema,
    showDeleteButton,
    setShowDeleteButton
  };
};

// 创建默认的 store 实例（向后兼容）

export const currentEditorSignal = createCurrentEditorSignal();
