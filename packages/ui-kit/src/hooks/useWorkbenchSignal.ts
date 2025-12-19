import { currentEditorSignal } from '../signals/current_editor';
import { useWorkbenchEditorSignal } from '../signals/workbench_editor';

/**
 * 工作台编辑器的Signal Hook
 */
export function useWorkbenchSignal() {
  // 工作台组件数据
  const {
    workbenchComponents,
    setWorkbenchComponents,
    loadWorkbenchComponents,
    addWorkbenchComponent,
    delWorkbenchComponents,
    batchDelWorkbenchComponents,
    clearWorkbenchComponents,
    wbComponentSchemas,
    loadWbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    batchDelWbComponentSchemas,
    clearWbComponentSchemas
  } = useWorkbenchEditorSignal;

  // 工作台当前选中状态
  const {
    editMode,
    setEditMode,
    clearEditMode,
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema,
    setCurComponentSchema,
    // clearCurComponentSchema,
    showDeleteButton,
    setShowDeleteButton
  } = currentEditorSignal;

  return {
    // 当前选中状态
    editMode: editMode.value,
    setEditMode,
    clearEditMode,
    curComponentID: curComponentID.value,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema: curComponentSchema.value,
    setCurComponentSchema,
    // clearCurComponentSchema,
    showDeleteButton: showDeleteButton.value,
    setShowDeleteButton,

    // 工作台组件数据
    workbenchComponents: workbenchComponents.value,
    setWorkbenchComponents,
    loadWorkbenchComponents,
    addWorkbenchComponent,
    delWorkbenchComponents,
    batchDelWorkbenchComponents,
    clearWorkbenchComponents,

    // 组件配置Schema
    wbComponentSchemas: wbComponentSchemas.value,
    loadWbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    batchDelWbComponentSchemas,
    clearWbComponentSchemas
  };
}
