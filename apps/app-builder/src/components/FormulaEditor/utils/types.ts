import type { functionType } from "./formula";

export interface FormulaEditorProps {
  visible: boolean;
  fieldName?: string;
  onCancel: () => void;
  onConfirm: (formula: string, formattedFormula: string, params: any) => void;
  initialFormula?: string;
}

export interface Variable {
  value: string;
  name: string;
  type: string;
  category: string;
}

export interface FunctionListProps {
  functionLoading: boolean;
  functions: functionGroup[]; //函数项数组，包含所有可展示的函数
  searchValue: string; // 搜索框的值，用于过滤函数列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onChooseFunction: (func: FunctionItem) => void; // 选择函数回调，用于将选中的函数传递给父组件
}

export interface functionGroup {
  type: functionType;
  functions: FunctionItem[];
}

export interface FunctionItem {
  id: string;
  name: string;
  summary: string;
  type: string;
  expression: string;
}

export interface info {
  example: string;
  usage: string;
  summary: string;
  name: string;
}
