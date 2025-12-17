import type { GridItem } from '@onebase/ui-kit';
import type { EditorProps } from '@/common/props';

/**
 * 编辑器工作区组件 Props
 */
export interface EditorWorkspaceProps {
  props: EditorProps & {
    useEditorSignalMap?: Map<string, any>;
    batchDelPageComponentSchemas?: (componentIds: Set<string>) => void;
    usePageViewEditorSignal?: () => Map<string, any>;
    workbenchComponents: GridItem[];
    setWorkbenchComponents: (items: GridItem[]) => void;
    wbComponentSchemas: Record<string, any>;
    setWbComponentSchemas: (id: string, schema: any) => void;
    delWbComponentSchemas: (id: string) => void;
    delWorkbenchComponents: (id: string) => void;
    // 组件选中相关
    curComponentID?: string | null;
    setCurComponentID: (id: string) => void;
    clearCurComponentID: () => void;
    setCurComponentSchema: (schema: any) => void;
    showDeleteButton?: boolean;
    setShowDeleteButton: (show: boolean) => void;
  };
}

/**
 * 工作台组件操作类型
 */
export interface WorkbenchComponentOperation {
  show: (componentId: string) => void;
  copy: (comp: GridItem, originId: string) => void;
  delete: (componentId: string) => void;
  widthChange: (componentId: string, newWidth: string) => void;
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
