// 交互动作类型枚举，和 index.tsx 27-54 保持一致
export enum InteractionActionType {
  Show = 'show',
  Hide = 'hide',
  Editable = 'editable',
  Readonly = 'readonly',
  Required = 'required',
  ReadonlyAll = 'readonly_all',
  SetFieldValue = 'set_field_value'
}
