export interface BaseNode {
  id: string;
  title: string;
  type: string;
}

export interface Condition {
  id: string;
  // AND 、OR
  condition?: string;
  // 父级id
  parentId: string;
  // 字段id
  fieldId?: string;

  // 操作符
  op?: string;
  // 对应操作类型
  operatorType?: string;
  // 操作值
  value?: string[];
  // 递归嵌套
  rules?: Condition[];
}

export enum FieldType {
  // 值
  VALUE = 'value',
  // 变量
  VARIABLES = 'variables',

  // 公式
  FORMULA = 'formula'
}
