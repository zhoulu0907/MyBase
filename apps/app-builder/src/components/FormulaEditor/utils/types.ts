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
