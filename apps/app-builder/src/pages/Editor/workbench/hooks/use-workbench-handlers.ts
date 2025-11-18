import { cloneDeep } from 'lodash-es';
import type { GridItem } from '@onebase/ui-kit';
import { STATUS_OPTIONS, STATUS_VALUES, getComponentSchema } from '@onebase/ui-kit';
import { WORKBENCH_DEFAULT_WIDTHS } from '../utils/constants';
import { getDefaultWidth } from '../utils/width-utils';
import type { WorkbenchComponentSchema } from '../types/workbenchComponent';

interface UseWorkbenchHandlersParams {
  pageComponentSchemas: Record<string, WorkbenchComponentSchema>;
  setPageComponentSchemas: (id: string, schema: WorkbenchComponentSchema) => void;
  delPageComponentSchemas: (id: string) => void;
  setComponents: (components: GridItem[]) => void;
  setCurComponentID: (id: string) => void;
  clearCurComponentID: () => void;
  setCurComponentSchema: (schema: WorkbenchComponentSchema) => void;
  setShowDeleteButton: (show: boolean) => void;
  components: GridItem[];
}

/**
 * 工作台事件处理 Hook
 */
export function useWorkbenchHandlers({
  pageComponentSchemas,
  setPageComponentSchemas,
  delPageComponentSchemas,
  setComponents,
  setCurComponentID,
  clearCurComponentID,
  setCurComponentSchema,
  setShowDeleteButton,
  components
}: UseWorkbenchHandlersParams) {
  // 取消隐藏组件
  const handleShowComponent = (componentId: string) => {
    const schema = pageComponentSchemas[componentId];
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
    setPageComponentSchemas(componentId, schema);
    setCurComponentID(componentId);
    setCurComponentSchema(schema);
    setShowDeleteButton(false);
  };

  // 复制组件
  const handleCopyComponent = (comp: GridItem, originId: string) => {
    const originSchema = pageComponentSchemas[originId];
    const newSchema = cloneDeep(originSchema);
    newSchema.config.id = comp.id;
    newSchema.config.cpName = comp.displayName;
    setPageComponentSchemas(comp.id, newSchema);
    setCurComponentID(comp.id);
    setCurComponentSchema(newSchema);
    setShowDeleteButton(false);
  };

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    delPageComponentSchemas(componentId);
    const newComponents = components.filter((cp: GridItem) => cp.id !== componentId);
    setComponents(newComponents);
    clearCurComponentID();
    setShowDeleteButton(false);
  };

  // 处理宽度变化
  const handleWidthChange = (componentId: string, newWidth: string) => {
    const schema = pageComponentSchemas[componentId];
    if (schema && schema.config) {
      schema.config.width = newWidth;
      setPageComponentSchemas(componentId, schema);
    }
  };

  // 选择组件
  const handleSelectComponent = (componentId: string, component: GridItem) => {
    setCurComponentID(componentId);
    const curComponentSchema = {
      id: componentId,
      type: component.type,
      displayName: component.displayName,
      ...pageComponentSchemas[componentId]
    };
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  // 处理组件拖入
  const handleComponentAdd = async (e: { item: HTMLElement }) => {
    const cpID = e.item.id || e.item.getAttribute('data-cp-id');
    const itemType = e.item.getAttribute('data-cp-type');
    const itemDisplayName = e.item.getAttribute('data-cp-displayname');

    console.log(`拖入工作台组件 ${cpID},类型 ${itemType}, 名称 ${itemDisplayName}`);

    if (cpID) {
      const cpSchema = pageComponentSchemas[cpID];
      if (cpSchema && cpSchema.config && cpSchema.editData) {
        console.log(`组件 ${cpID} 已存在，不进行创建`);
        setCurComponentID(cpID);
        setCurComponentSchema(cpSchema);
        setShowDeleteButton(false);
        return;
      }
    }

    const schema = getComponentSchema(itemType as string) as WorkbenchComponentSchema;
    schema.config.cpName = itemDisplayName || undefined;
    schema.config.id = cpID ? cpID : undefined;
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

    // 确保宽度已设置（如果schema中没有宽度，使用默认值）
    if (!schema.config.width) {
      schema.config.width = getDefaultWidth(itemType as string, WORKBENCH_DEFAULT_WIDTHS);
    }

    if (cpID) {
      setPageComponentSchemas(cpID, schema);
      setCurComponentID(cpID);
      setCurComponentSchema(schema);
      setShowDeleteButton(false);
    }
  };

  // 处理拖拽开始
  const handleDragStart = (e: { item: HTMLElement }) => {
    const cpID = e.item.getAttribute('data-cp-id') || '';
    setCurComponentID(cpID);
    const curComponentSchema = pageComponentSchemas[cpID] || {};
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  return {
    handleShowComponent,
    handleCopyComponent,
    handleDeleteComponent,
    handleWidthChange,
    handleSelectComponent,
    handleComponentAdd,
    handleDragStart
  };
}
