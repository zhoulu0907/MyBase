import type { Diagnostic } from '@codemirror/lint';

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

// 自定义语法检查器
export const validateFormula = (text: string) => {
  const diagnostics: Diagnostic[] = [];
  const quoteStack: string[] = [];

  // 1. 检测引号不匹配（单/双引号）
  for (let i = 0; i < text.length; i++) {
    const char = text[i];
    if (char === '"' || char === "'") {
      quoteStack.length === 0 || quoteStack.at(-1) !== char ? quoteStack.push(char) : quoteStack.pop();
    }
  }
  if (quoteStack.length > 0) {
    diagnostics.push({
      from: text.length,
      to: text.length,
      severity: 'error',
      message: `未闭合的${quoteStack[0] === '"' ? '双引号' : '单引号'}`
    });
  }

  // 2. 检测LaTeX命令未闭合（如\frac{ 缺少 }）
  const unclosedCmd = /\\[a-zA-Z]+\{(?![^}]*\})/g;
  let match;
  while ((match = unclosedCmd.exec(text))) {
    diagnostics.push({
      from: match.index,
      to: match.index + match[0].length,
      severity: 'error',
      message: `LaTeX命令 "${match[0]}" 缺少闭合符号 "}"`
    });
  }
  return diagnostics;
};
