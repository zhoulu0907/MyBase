/**
 * Workbench 组件类型常量与元数据
 * 统一管理组件类型定义、展示信息与默认配置
 */

type WorkbenchComponentDefinition = {
  key: string;
  type: `X${string}`;
  displayName: string;
  icon: string;
  category: WorkbenchComponentCategory;
  size: { h: number; w: number };
  description?: string;
  isPlaceholder?: boolean;
};

const WORKBENCH_COMPONENT_DEFINITIONS: ReadonlyArray<WorkbenchComponentDefinition> = [
  {
    key: 'QUICK_ENTRY',
    type: 'XQuickEntry',
    displayName: '快捷入口',
    icon: 'quick_entry_cp.svg',
    category: 'basic',
    size: { h: 36, w: 118 }
  },
  // {
  //   key: 'TODO_CENTER',
  //   type: 'XTodoCenter',
  //   displayName: '待办中心',
  //   icon: 'todo_center_cp.svg',
  //   category: 'basic',
  //   size: { h: 36, w: 118 },
  //   description: '待办中心组件开发中，当前为占位实现',
  //   isPlaceholder: true
  // },
  {
    key: 'RICH_TEXT_WORKBENCH',
    type: 'XRichTextEditorWorkbench',
    displayName: '富文本',
    icon: 'rich_text_editor_workbench_cp.svg',
    category: 'basic',
    size: { h: 36, w: 118 },
    description: '富文本编辑器组件开发中，当前为占位实现',
    isPlaceholder: true
  },
  {
    key: 'CAROUSEL_WORKBENCH',
    type: 'XCarouselWorkbench',
    displayName: '轮播图',
    icon: 'carousel_workbench_cp.svg',
    category: 'advanced',
    size: { h: 36, w: 118 }
  },
  {
    key: 'BUTTON_WORKBENCH',
    type: 'XButtonWorkbench',
    displayName: '按钮',
    icon: 'button_workbench_cp.svg',
    category: 'basic',
    size: { h: 36, w: 118 }
  }
] as const;

type WorkbenchComponentKey = (typeof WORKBENCH_COMPONENT_DEFINITIONS)[number]['key'];

// 工作台基础组件类型
export const WORKBENCH_COMPONENT_TYPES = WORKBENCH_COMPONENT_DEFINITIONS.reduce((acc, def) => {
  acc[def.key as WorkbenchComponentKey] = def.type;
  return acc;
}, {} as Record<WorkbenchComponentKey, (typeof WORKBENCH_COMPONENT_DEFINITIONS)[number]['type']>) as {
  [K in WorkbenchComponentKey]: Extract<
    (typeof WORKBENCH_COMPONENT_DEFINITIONS)[number],
    { key: K }
  >['type'];
};

// 所有工作台组件类型
export const ALL_WORKBENCH_COMPONENT_TYPES = {
  ...WORKBENCH_COMPONENT_TYPES
} as const;

// 组件类型联合类型
export type WorkbenchComponentType =
  (typeof ALL_WORKBENCH_COMPONENT_TYPES)[keyof typeof ALL_WORKBENCH_COMPONENT_TYPES];

export type WorkbenchComponentCategory = 'basic' | 'advanced';

export interface WorkbenchComponentMeta {
  type: WorkbenchComponentType;
  displayName: string;
  icon: string;
  category: WorkbenchComponentCategory;
  size: {
    h: number;
    w: number;
  };
  description?: string;
  isPlaceholder?: boolean;
}

export const WORKBENCH_COMPONENT_META: Record<WorkbenchComponentType, WorkbenchComponentMeta> =
  WORKBENCH_COMPONENT_DEFINITIONS.reduce((acc, def) => {
    acc[def.type as WorkbenchComponentType] = {
      type: def.type as WorkbenchComponentType,
      displayName: def.displayName,
      icon: def.icon,
      category: def.category,
      size: def.size,
      description: def.description,
      isPlaceholder: def.isPlaceholder
    };
    return acc;
  }, {} as Record<WorkbenchComponentType, WorkbenchComponentMeta>);

export const WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP: Record<WorkbenchComponentType, string> =
  Object.values(WORKBENCH_COMPONENT_META).reduce(
    (acc, meta) => {
      acc[meta.type] = meta.displayName;
      return acc;
    },
    {} as Record<WorkbenchComponentType, string>
  );

// 组件类型值数组（用于类型检查）
export const WORKBENCH_COMPONENT_TYPE_VALUES = WORKBENCH_COMPONENT_DEFINITIONS.map(
  (def) => def.type as WorkbenchComponentType
);

