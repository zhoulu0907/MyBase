export enum functionType {
  COMMON = 'COMMON',
  TEXT = 'TEXT',
  NUMBER = 'NUMBER',
  LOGIC = 'LOGIC',
  DATE = 'DATE',
  USER = 'USER'
}

export enum functionlabel {
  COMMON = '常用函数',
  TEXT = '文本函数',
  NUMBER = '数学函数',
  LOGIC = '逻辑函数',
  DATE = '日期函数',
  USER = '人员函数'
}

export const funtionGroupList = [
  { type: functionType.COMMON, label: functionlabel.COMMON },
  { type: functionType.TEXT, label: functionlabel.TEXT },
  { type: functionType.NUMBER, label: functionlabel.NUMBER },
  { type: functionType.LOGIC, label: functionlabel.LOGIC },
  { type: functionType.DATE, label: functionlabel.DATE },
  { type: functionType.USER, label: functionlabel.USER }
];
