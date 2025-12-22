import { getDefaultWidth } from '../utils/width-utils';
import { WORKBENCH_DEFAULT_WIDTHS } from '../utils/constants';
import { cloneDeep } from 'lodash-es';
import type { GridItem } from '@onebase/ui-kit';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  type WorkbenchComponentType,
  getWorkbenchComponentSchema
} from '@onebase/ui-kit';
import type { WorkbenchComponentSchema } from '../types/workbench-component';

interface UseWorkbenchHandlersParams {
  wbComponentSchemas: Record<string, any>;
  setWbComponentSchemas: (id: string, schema: any) => void;
  delWbComponentSchemas: (id: string) => void;
  setWorkbenchComponents: (component: GridItem[]) => void;
  delWorkbenchComponents: (id: string) => void;
  setCurComponentID: (id: string) => void;
  clearCurComponentID: () => void;
  setCurComponentSchema: (schema: any) => void;
  setShowDeleteButton: (show: boolean) => void;
  workbenchComponents: GridItem[];
}

/**
 * 工作台事件处理 Hook (移动端版本，包含复杂的entity处理逻辑)
 */
export function useWorkbenchHandlers({
  wbComponentSchemas,
  setWbComponentSchemas,
  delWbComponentSchemas,
  setWorkbenchComponents,
  delWorkbenchComponents,
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

  // 复制组件 (保留移动端的递归逻辑)
  const handleCopyComponent = (comp: any, originId: string) => {
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
    delWorkbenchComponents(componentId);
    delWbComponentSchemas(componentId);

    // 如果删除的是当前选中的组件，清除选中状态
    if (wbComponentSchemas[componentId]) {
      clearCurComponentID();
    }
  };

  // 选择组件
  const handleSelectComponent = (componentId: string) => {
    setCurComponentID(componentId);
    const curComponentSchema = wbComponentSchemas[componentId] || {};
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  // 处理拖拽开始
  const handleDragStart = (e: { item: HTMLElement }) => {
    const cpID = e.item.getAttribute('data-cp-id') || '';
    setCurComponentID(cpID);
    const curComponentSchema = wbComponentSchemas[cpID] || {};
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  // 获取或创建页面配置 schema
  const getPageConfigSchema = () => {
    return {
      type: 'page',
      config: {
        showHeader: true,
        showSidebar: true
      }
    };
  };

  // 处理空白区域点击
  const handleBodyMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    const target = e.target as HTMLElement;
    if (target.id === 'workspace-content') {
      clearCurComponentID?.();
      setShowDeleteButton(false);
      // 设置页面配置 schema
      const pageConfigSchema = getPageConfigSchema();
      setCurComponentSchema(pageConfigSchema);
    }
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

  return {
    handleShowComponent,
    handleCopyComponent,
    handleDeleteComponent,
    handleSelectComponent,
    handleDragStart,
    handleBodyMouseDown,
    getPageConfigSchema,
    handleComponentAdd
  };
}
