import { cloneDeep } from 'lodash-es';
import type { GridItem } from '@onebase/ui-kit';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  getWorkbenchComponentSchema,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { WORKBENCH_DEFAULT_WIDTHS } from '../utils/constants';
import { getDefaultWidth } from '../utils/width-utils';
import type { WorkbenchComponentSchema } from '../types/workbench-component';

interface UseWorkbenchHandlersParams {
  wbComponentSchemas: Record<string, WorkbenchComponentSchema>;
  setWbComponentSchemas: (id: string, schema: WorkbenchComponentSchema) => void;
  delWbComponentSchemas: (id: string) => void;
  setWorkbenchComponents: (workbenchComponents: GridItem[]) => void;
  setCurComponentID: (id: string) => void;
  clearCurComponentID: () => void;
  setCurComponentSchema: (schema: WorkbenchComponentSchema) => void;
  setShowDeleteButton: (show: boolean) => void;
  workbenchComponents: GridItem[];
}

/**
 * 工作台事件处理 Hook
 */
export function useWorkbenchHandlers({
  wbComponentSchemas,
  setWbComponentSchemas,
  delWbComponentSchemas,
  setWorkbenchComponents,
  setCurComponentID,
  clearCurComponentID,
  setCurComponentSchema,
  setShowDeleteButton,
  workbenchComponents
}: UseWorkbenchHandlersParams) {
  // 取消隐藏组件
  const handleShowComponent = (componentId: string) => {
    const schema = wbComponentSchemas[componentId];
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
    setWbComponentSchemas(componentId, schema);
    setCurComponentID(componentId);
    setCurComponentSchema(schema);
    setShowDeleteButton(false);
  };

  // 复制组件
  const handleCopyComponent = (comp: GridItem, originId: string) => {
    // 1. 将新组件添加到组件列表
    const newComponents = [...workbenchComponents, comp];
    setWorkbenchComponents(newComponents);

    // 2. 获取原始组件的配置
    const originSchema = wbComponentSchemas[originId];
    if (!originSchema) {
      console.warn(`未找到原始组件 ${originId} 的配置`);
      return;
    }

    // 3. 创建新的组件 schema
    const schema = getWorkbenchComponentSchema(comp.type as WorkbenchComponentType) as WorkbenchComponentSchema;

    // 4. 获取原始组件的配置并合并：使用原始组件的配置覆盖默认配置
    const originConfig = originSchema.config || {};
    schema.config = {
      ...schema.config,
      ...originConfig,
      id: comp.id, // 使用新 ID
      cpName: comp.displayName || '' // 使用新名称
    };

    // 5. 如果原始组件有 editData，也需要复制
    if (originSchema.editData) {
      schema.editData = cloneDeep(originSchema.editData);
    }

    // 6. 构建完整的 props（WorkbenchComponentSchema 不包含 id 和 type，这些在 config 中）
    const newProps: WorkbenchComponentSchema = {
      ...schema
    };

    // 7. 保存新组件配置
    setWbComponentSchemas(comp.id, newProps);

    // 8. 设置当前组件
    setCurComponentID(comp.id);
    setCurComponentSchema(newProps);
    setShowDeleteButton(false);
  };

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    delWbComponentSchemas(componentId);
    const newComponents = workbenchComponents.filter((cp: GridItem) => cp.id !== componentId);
    setWorkbenchComponents(newComponents);
    clearCurComponentID();
    setShowDeleteButton(false);
  };

  // 处理宽度变化
  const handleWidthChange = (componentId: string, newWidth: string) => {
    const schema = wbComponentSchemas[componentId];
    if (schema && schema.config) {
      schema.config.width = newWidth;
      setWbComponentSchemas(componentId, schema);
    }
  };

  // 选择组件
  const handleSelectComponent = (componentId: string, component: GridItem) => {
    setCurComponentID(componentId);
    const curComponentSchema = {
      id: componentId,
      type: component.type,
      displayName: component.displayName,
      ...wbComponentSchemas[componentId]
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
      const cpSchema = wbComponentSchemas[cpID];
      if (cpSchema && cpSchema.config && cpSchema.editData) {
        console.log(`组件 ${cpID} 已存在，不进行创建`);
        setCurComponentID(cpID);
        setCurComponentSchema(cpSchema);
        setShowDeleteButton(false);
        return;
      }
    }

    const schema = getWorkbenchComponentSchema(itemType as WorkbenchComponentType) as WorkbenchComponentSchema;
    schema.config.cpName = itemDisplayName || undefined;
    schema.config.id = cpID ? cpID : undefined;
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

    // 确保宽度已设置（如果schema中没有宽度，使用默认值）
    if (!schema.config.width) {
      schema.config.width = getDefaultWidth(itemType as string, WORKBENCH_DEFAULT_WIDTHS);
    }

    if (cpID) {
      setWbComponentSchemas(cpID, schema);
      setCurComponentID(cpID);
      setCurComponentSchema(schema);
      setShowDeleteButton(false);
    }
  };

  // 处理拖拽开始
  const handleDragStart = (e: { item: HTMLElement }) => {
    const cpID = e.item.getAttribute('data-cp-id') || '';
    setCurComponentID(cpID);
    const curComponentSchema = wbComponentSchemas[cpID] || {};
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
