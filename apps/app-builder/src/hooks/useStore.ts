import { EDITOR_TYPES } from "@/pages/Editor/components/const";
import {
  useBasicEditorStore,
  useFromEditorStore,
  useListEditorStore,
} from "@/store";
import { useLocation } from "react-router-dom";

export function usePageEditorStore() {
  const path = useLocation().pathname;
  const useList = path.endsWith(`/${EDITOR_TYPES.LIST_EDITOR}`);

  const curComponentID = useBasicEditorStore((state) => state.curComponentID);
  const setCurComponentID = useBasicEditorStore(
    (state) => state.setCurComponentID,
  );
  const clearCurComponentID = useBasicEditorStore(
    (state) => state.clearCurComponentID,
  );
  const curComponentSchema = useBasicEditorStore(
    (state) => state.curComponentSchema,
  );
  const setCurComponentSchema = useBasicEditorStore(
    (state) => state.setCurComponentSchema,
  );
  const showDeleteButton = useBasicEditorStore(
    (state) => state.showDeleteButton,
  );
  const setShowDeleteButton = useBasicEditorStore(
    (state) => state.setShowDeleteButton,
  );

  // 补齐所有 @file_context_0 中定义的 store 字段和方法
  const components = useList
    ? useListEditorStore((state) => state.components)
    : useFromEditorStore((state) => state.components);

  const setComponents = useList
    ? useListEditorStore((state) => state.setComponents)
    : useFromEditorStore((state) => state.setComponents);

  const delComponents = useList
    ? useListEditorStore((state) => state.delComponents)
    : useFromEditorStore((state) => state.delComponents);

  const clearComponents = useList
    ? useListEditorStore((state) => state.clearComponents)
    : useFromEditorStore((state) => state.clearComponents);

  const pageComponentSchemas = useList
    ? useListEditorStore((state) => state.pageComponentSchemas)
    : useFromEditorStore((state) => state.pageComponentSchemas);

  const setPageComponentSchemas = useList
    ? useListEditorStore((state) => state.setPageComponentSchemas)
    : useFromEditorStore((state) => state.setPageComponentSchemas);

  const delPageComponentSchemas = useList
    ? useListEditorStore((state) => state.delPageComponentSchemas)
    : useFromEditorStore((state) => state.delPageComponentSchemas);

  const clearPageComponentSchemas = useList
    ? useListEditorStore((state) => state.clearPageComponentSchemas)
    : useFromEditorStore((state) => state.clearPageComponentSchemas);

  const colComponentsMap = useList
    ? useListEditorStore((state) => state.colComponentsMap)
    : useFromEditorStore((state) => state.colComponentsMap);

  const setColComponentsMap = useList
    ? useListEditorStore((state) => state.setColComponentsMap)
    : useFromEditorStore((state) => state.setColComponentsMap);

  const delColComponentsMap = useList
    ? useListEditorStore((state) => state.delColComponentsMap)
    : useFromEditorStore((state) => state.delColComponentsMap);

  const clearColComponentsMap = useList
    ? useListEditorStore((state) => state.clearColComponentsMap)
    : useFromEditorStore((state) => state.clearColComponentsMap);

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
    colComponentsMap,
    setColComponentsMap,
    delColComponentsMap,
    clearColComponentsMap,
  };
}
