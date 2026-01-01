import { EDITOR_TYPES } from 'src/utils';
import { currentEditorSignal } from '../signals/current_editor';
import { useFormEditorSignal, useListEditorSignal } from '../signals/page_editor';

import { useLocation } from 'react-router-dom';

export function usePageEditorSignal(pageType?: string) {
  const path = useLocation().pathname;

  /**
   * 表单设计页、新增、编辑、详情页  使用 useFormEditorSignal
   * 其余  使用 useListEditorSignal
   */
  const useList = !(
    (pageType && pageType.indexOf(EDITOR_TYPES.FORM_EDITOR) !== -1) ||
    path.endsWith(`/${EDITOR_TYPES.FORM_EDITOR}`)
  );

  const editorSignal = useList ? useListEditorSignal : useFormEditorSignal;

  // 优化：避免多次调用 createCurrentEditorSignal，提升性能和可读性
  const curComponentID = currentEditorSignal.curComponentID.value;
  const curComponentSchema = currentEditorSignal.curComponentSchema.value;
  const showDeleteButton = currentEditorSignal.showDeleteButton.value;

  const { setCurComponentID, setCurComponentSchema, clearCurComponentID, setShowDeleteButton } = currentEditorSignal;

  // 统一从缓存的 signal 对象中获取所有属性，避免重复判断
  const components = editorSignal.components.value;

  const setComponents = editorSignal.setComponents;

  const delComponents = editorSignal.delComponents;

  const addComponents = editorSignal.addComponents;

  const clearComponents = editorSignal.clearComponents;

  const pageComponentSchemas = editorSignal.pageComponentSchemas.value;

  const loadPageComponentSchemas = editorSignal.loadPageComponentSchemas;

  const setPageComponentSchemas = editorSignal.setPageComponentSchemas;

  const delPageComponentSchemas = editorSignal.delPageComponentSchemas;

  const batchDelPageComponentSchemas = editorSignal.batchDelPageComponentSchemas;

  const clearPageComponentSchemas = editorSignal.clearPageComponentSchemas;

  const layoutSubComponents = editorSignal.layoutSubComponents.value;

  const loadLayoutSubComponents = editorSignal.loadLayoutSubComponents;

  const setLayoutSubComponents = editorSignal.setLayoutSubComponents;

  const delLayoutSubComponents = editorSignal.delLayoutSubComponents;

  const batchDelLayoutSubComponents = editorSignal.batchDelLayoutSubComponents;

  const clearLayoutSubComponents = editorSignal.clearLayoutSubComponents;

  // 子表单
  const subTableComponents = editorSignal.subTableComponents.value;

  const setSubTableComponents = editorSignal.setSubTableComponents;

  const delSubTableComponents = editorSignal.delSubTableComponents;

  const batchDelSubTableComponents = editorSignal.batchDelSubTableComponents;

  const clearSubTableComponents = editorSignal.clearSubTableComponents;

  return {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema,
    setCurComponentSchema,
    showDeleteButton,
    setShowDeleteButton,
    components,
    setComponents,
    addComponents,
    delComponents,
    clearComponents,
    pageComponentSchemas,
    loadPageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    batchDelPageComponentSchemas,
    clearPageComponentSchemas,
    layoutSubComponents,
    loadLayoutSubComponents,
    setLayoutSubComponents,
    delLayoutSubComponents,
    batchDelLayoutSubComponents,
    clearLayoutSubComponents,
    subTableComponents,
    setSubTableComponents,
    delSubTableComponents,
    batchDelSubTableComponents,
    clearSubTableComponents
  };
}
