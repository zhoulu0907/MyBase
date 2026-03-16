import type { GridItem } from '@onebase/ui-kit';

/**
 * 工作台组件操作类型
 */
export interface WorkbenchComponentOperation {
  show: (componentId: string) => void;
  copy: (comp: GridItem, originId: string) => void;
  delete: (componentId: string) => void;
  widthChange: (componentId: string, newWidth: string) => void;
  heightChange: (componentId: string, rowSpan: number) => void;
  select: (componentId: string, component: GridItem) => void;
}

/**
 * 工作台组件 Schema 配置
 */
export interface WorkbenchComponentSchema {
  config: {
    id?: string;
    cpName?: string;
    status?: string;
    width?: string;
    rowSpan?: number;
    gridLayout?: { row: number; column: number; rowSpan: number; colSpan: number };
  };
  editData?: unknown;
}

/**
 * 工作台组件项属性
 */
export interface WorkbenchItemProps {
  component: GridItem;
  isSelected: boolean;
  currentWidth: string;
  containerWidth: number;
  pageComponentSchema: WorkbenchComponentSchema;
  onOperation: WorkbenchComponentOperation;
}
