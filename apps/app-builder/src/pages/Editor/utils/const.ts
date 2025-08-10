export const COMPONENT_GROUP_NAME = 'component-list';

export interface GridItem {
    id: string;
    type: string;
    displayName: string;
}

// 编辑器类型常量
export const EDITOR_TYPES = {
  FORM_EDITOR: 'form_editor',
  LIST_EDITOR: 'list_editor',
  PAGE_SETTING: 'page_setting',
  METADATA_MANAGE: 'metadata_manage'
} as const;

// 编辑器类型联合类型
export type EditorType = (typeof EDITOR_TYPES)[keyof typeof EDITOR_TYPES];
