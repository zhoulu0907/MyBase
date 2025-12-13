import { EDITOR_TYPES } from 'src/utils';
import { currentEditorSignal } from '../signals/current_editor';
import { useFormEditorSignal, useListEditorSignal } from '../signals/page_editor';

import { useLocation } from 'react-router-dom';

export function usePageEditorSignal(pageType?:string) {

  const path = useLocation().pathname;
  
  /**
   * 表单设计页、新增、编辑、详情页  使用 useFormEditorSignal
   * 其余  使用 useListEditorSignal
   */
  const useList = !(pageType && pageType.indexOf(EDITOR_TYPES.FORM_EDITOR) !== -1 ||
    path.endsWith(`/${EDITOR_TYPES.FORM_EDITOR}`));

  // 优化：避免多次调用 createCurrentEditorSignal，提升性能和可读性
  const curComponentID = currentEditorSignal.curComponentID.value;
  const curComponentSchema = currentEditorSignal.curComponentSchema.value;
  const showDeleteButton = currentEditorSignal.showDeleteButton.value;

  const { setCurComponentID, setCurComponentSchema, clearCurComponentID, setShowDeleteButton } = currentEditorSignal;

  const components = useList ? useListEditorSignal.components.value : useFormEditorSignal.components?.value;

  const setComponents = useList ? useListEditorSignal.setComponents : useFormEditorSignal.setComponents;

  const delComponents = useList ? useListEditorSignal.delComponents : useFormEditorSignal.delComponents;

  const addComponents = useList ? useListEditorSignal.addComponents : useFormEditorSignal.addComponents;

  const clearComponents = useList ? useListEditorSignal.clearComponents : useFormEditorSignal.clearComponents;

  const pageComponentSchemas = useList
    ? useListEditorSignal.pageComponentSchemas.value
    : useFormEditorSignal.pageComponentSchemas?.value;

  const loadPageComponentSchemas = useList
    ? useListEditorSignal.loadPageComponentSchemas
    : useFormEditorSignal.loadPageComponentSchemas;

  const setPageComponentSchemas = useList
    ? useListEditorSignal.setPageComponentSchemas
    : useFormEditorSignal.setPageComponentSchemas;

  const delPageComponentSchemas = useList
    ? useListEditorSignal.delPageComponentSchemas
    : useFormEditorSignal.delPageComponentSchemas;

  const batchDelPageComponentSchemas = useList
    ? useListEditorSignal.batchDelPageComponentSchemas
    : useFormEditorSignal.batchDelPageComponentSchemas;

  const clearPageComponentSchemas = useList
    ? useListEditorSignal.clearPageComponentSchemas
    : useFormEditorSignal.clearPageComponentSchemas;

  const layoutSubComponents = useList
    ? useListEditorSignal.layoutSubComponents.value
    : useFormEditorSignal.layoutSubComponents?.value;

  const loadLayoutSubComponents = useList
    ? useListEditorSignal.loadLayoutSubComponents
    : useFormEditorSignal.loadLayoutSubComponents;

  const setLayoutSubComponents = useList
    ? useListEditorSignal.setLayoutSubComponents
    : useFormEditorSignal.setLayoutSubComponents;

  const delLayoutSubComponents = useList
    ? useListEditorSignal.delLayoutSubComponents
    : useFormEditorSignal.delLayoutSubComponents;

  const batchDelLayoutSubComponents = useList
    ? useListEditorSignal.batchDelLayoutSubComponents
    : useFormEditorSignal.batchDelLayoutSubComponents;

  const clearLayoutSubComponents = useList
    ? useListEditorSignal.clearLayoutSubComponents
    : useFormEditorSignal.clearLayoutSubComponents;

  // 子表单
  const subTableComponents = useList
    ? useListEditorSignal.subTableComponents.value
    : useFormEditorSignal.subTableComponents?.value;

  const setSubTableComponents = useList
    ? useListEditorSignal.setSubTableComponents
    : useFormEditorSignal.setSubTableComponents;

  const delSubTableComponents = useList
    ? useListEditorSignal.delSubTableComponents
    : useFormEditorSignal.delSubTableComponents;

  const batchDelSubTableComponents = useList
    ? useListEditorSignal.batchDelSubTableComponents
    : useFormEditorSignal.batchDelSubTableComponents;

  const clearSubTableComponents = useList
    ? useListEditorSignal.clearSubTableComponents
    : useFormEditorSignal.clearSubTableComponents;

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
    clearSubTableComponents,
  };
}
