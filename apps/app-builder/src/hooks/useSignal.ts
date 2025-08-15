
import { EDITOR_TYPES } from '@/pages/Editor/utils/const';
import { currentEditorSignal } from '@/store/singals/current_editor';
import { useFormEditorSignal, useListEditorSignal } from '@/store/singals/page_editor';

import { useLocation } from 'react-router-dom';

export function usePageEditorSignal() {
  const path = useLocation().pathname;
  const useList = path.endsWith(`/${EDITOR_TYPES.LIST_EDITOR}`);

  // 优化：避免多次调用 createCurrentEditorSignal，提升性能和可读性
  const curComponentID = currentEditorSignal.curComponentID.value;
  const curComponentSchema = currentEditorSignal.curComponentSchema.value;
  const showDeleteButton = currentEditorSignal.showDeleteButton.value;

  const { setCurComponentID,setCurComponentSchema, clearCurComponentID, setShowDeleteButton } = currentEditorSignal;


  const components = useList
    ? useListEditorSignal.components.value
    : useFormEditorSignal.components.value;

  const setComponents = useList
    ? useListEditorSignal.setComponents
    : useFormEditorSignal.setComponents;

  const delComponents = useList
    ? useListEditorSignal.delComponents
    : useFormEditorSignal.delComponents;

  const clearComponents = useList
    ? useListEditorSignal.clearComponents
    : useFormEditorSignal.clearComponents;

  const pageComponentSchemas = useList
    ? useListEditorSignal.pageComponentSchemas.value
    : useFormEditorSignal.pageComponentSchemas.value;

  const setPageComponentSchemas = useList
    ? useListEditorSignal.setPageComponentSchemas
    : useFormEditorSignal.setPageComponentSchemas;

  const delPageComponentSchemas = useList
    ? useListEditorSignal.delPageComponentSchemas
    : useFormEditorSignal.delPageComponentSchemas;

  const clearPageComponentSchemas = useList
    ? useListEditorSignal.clearPageComponentSchemas
    : useFormEditorSignal.clearPageComponentSchemas;

  const layoutSubComponents = useList
    ? useListEditorSignal.layoutSubComponents.value
    : useFormEditorSignal.layoutSubComponents.value;

  const setLayoutSubComponents = useList
    ? useListEditorSignal.setLayoutSubComponents
    : useFormEditorSignal.setLayoutSubComponents;

  const delLayoutSubComponents = useList
    ? useListEditorSignal.delLayoutSubComponents
    : useFormEditorSignal.delLayoutSubComponents;

  const clearLayoutSubComponents = useList
    ? useListEditorSignal.clearLayoutSubComponents
    : useFormEditorSignal.clearLayoutSubComponents;

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
    delComponents,
    clearComponents,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    clearPageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    delLayoutSubComponents,
    clearLayoutSubComponents
  };
}
