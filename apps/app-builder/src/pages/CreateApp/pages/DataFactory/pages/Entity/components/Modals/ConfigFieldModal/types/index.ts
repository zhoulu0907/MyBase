import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { AUTO_CODE_RULE_TYPE } from '../utils/const';

// 重新导出EntityNode类型
export type { EntityNode };

// 字段表单值接口
export interface FieldFormValues {
  id?: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: number;
  isRequired: number;
  isSystemField: number;
  sortOrder?: number;
  isDeleted?: boolean;
  displayName?: string;
  options?: { optionLabel: string; optionValue: string }[];
  dictTypeId?: string; // 字典类型ID（用于引用字典）
  autoNumber?: AutoNumberRule; // 前端更新后的自动编号配置（提交给后端时使用）
  autoNumberConfig?: AutoNumberRule; // 后端返回的自动编号配置（用于初始化和回显）
  constraints?: {
    lengthEnabled: number;
    minLength: number;
    maxLength: number;
    lengthPrompt: string;
    regexEnabled: number;
    regexPattern: string;
    regexPrompt: string;
  };
  dataSelectionConfig?: DataSelectionType;
}

// 数据选择配置
export interface DataSelectionType {
  targetEntityId: string;
  targetFieldId: string;
}

// 字段配置弹窗属性
export interface ConfigFieldModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: Partial<EntityNode>;
  successCallback: () => void;
  initialFields?: FieldFormValues[];
  gotoDictPage?: () => void;
  entities?: EntityNode[];
}

// 字段操作接口
export interface FieldOperations {
  addField: () => void;
  deleteField: (id: string, onDelete?: (id: string) => void) => void;
  updateField: (id: string, updates: Partial<FieldFormValues>) => void;
  moveField: (oldIndex: number, newIndex: number) => void;
  getFieldById: (id: string) => FieldFormValues | undefined;
  getFieldIndex: (id: string) => number;
}

// 字段验证接口
export interface FieldValidation {
  validateField: (field: FieldFormValues) => string[];
  validateAllFields: (fields: FieldFormValues[]) => Record<string, string>;
  clearErrors: () => void;
  setFieldError: (fieldId: string, field: string, error: string) => void;
  setAllErrors: (errors: Record<string, string>) => void;
  errors: Record<string, string>;
}

// 字段数据管理接口
export interface FieldDataManager {
  fields: FieldFormValues[];
  activeFields: FieldFormValues[];
  originFields: any[];
  loading: boolean;
  errors: Record<string, string>;
  setFields: (fields: FieldFormValues[]) => void;
  refreshFields: () => Promise<void>;
}

// 表格列配置接口
export interface ColumnConfig {
  title: string | React.ReactNode;
  dataIndex: string;
  width?: number;
  ellipsis?: boolean;
  align?: 'center' | 'left' | 'right';
  render?: (value: unknown, record: FieldFormValues, index: number) => React.ReactNode;
}

// 字段配置弹窗属性
export interface FieldConfigPopoverProps {
  fieldType: string;
  fieldId: string;
  field: FieldFormValues;
  onConfirm: (fieldType: string, fieldId: string, configData: any, dictTypeId?: string) => void;
  onCancel: (fieldType: string) => void;
  fields: any[];
  gotoDictPage?: () => void;
  entities?: EntityNode[];
}

// 可排序表格属性
export interface SortableTableProps {
  data: FieldFormValues[];
  columns: ColumnConfig[];
  onSort: ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => void;
}

// 创建自动编号规则返回值
export interface AutoNumberRuleResponce {
  numberMode?: string;
  digitWidth: number;
  overflowContinue?: number;
  resetCycle?: string;
  resetOnInitialChange?: number;
  startValue?: number;
  initialValue?:number
}
// 创建自动编号规则
export interface AutoNumberRule {
  isEnabled: number;
  numberMode: string;
  digitWidth: number;
  overflowContinue: number;
  initialValue: number;
  resetCycle: string;
  resetOnInitialChange?: number;
  startValue?: number;
  rules: AutoNumberRuleItem[];
}

export interface AutoNumberRuleItem {
  id?: string;
  itemType:
    | typeof AUTO_CODE_RULE_TYPE.SEQUENCE
    | typeof AUTO_CODE_RULE_TYPE.DATE
    | typeof AUTO_CODE_RULE_TYPE.TEXT
    | typeof AUTO_CODE_RULE_TYPE.FIELD_REF;
  itemOrder?: number;
  isEnabled?: number;
  format?: string;
  textValue?: string;
}

export interface AutoCodeRule {
  id?: string;
  itemType:
    | typeof AUTO_CODE_RULE_TYPE.SEQUENCE
    | typeof AUTO_CODE_RULE_TYPE.DATE
    | typeof AUTO_CODE_RULE_TYPE.TEXT
    | typeof AUTO_CODE_RULE_TYPE.FIELD_REF;
  config: Record<string, unknown>;
}

export interface AutoCodeRules {
  rules: AutoCodeRule[];
}

// 资产及子表字段
export interface EntityFieldsWithChildren {
  label: string;
  value: string;
  children: { label: string; value: string; fieldType: string; isSystemField: number }[];
}
