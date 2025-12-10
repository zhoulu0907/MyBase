import { Alert } from '@arco-design/web-react';
import { useCallback, useRef, useEffect, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { EditorView } from '@codemirror/view';
import type { functionGroup } from '../utils/types';
import { placeholdersPlugin } from '../utils/placeholders';
import styles from './FormulaInput.module.less';
import { defaultExtenstion } from '../utils/defaultLine';
import type { VariablesList } from '@onebase/app';
import { lintGutter, linter, type Diagnostic } from '@codemirror/lint';
import { validateFormula } from '../utils/formula';

interface FormulaInputProps {
  value: string; // 当前公式的值
  fieldName?: string;
  isDebugMode: boolean;
  onChange: (value: string) => void; // 公式变化时的回调函数
  onCopy: () => void; // 复制成功后的回调函数
  onDebug: () => void; // 调试按钮点击回调函数
  filteredVariables: VariablesList[]; // 过滤后的变量列表
  filteredFunctions: functionGroup[]; // 过滤后的函数列表
  onEditorReady?: (editor: { insertAtPosition: (text: string, type?: string, position?: number) => void }) => void; // 编辑器就绪回调
}

interface FormulaError {
  message: string; // 错误信息
  from: number; // 错误起始位置
  to: number; // 错误结束位置
  severity: 'error' | 'warning'; // 错误严重程度
}

export function FormulaInput({
  value,
  fieldName,
  onChange,
  onCopy,
  onDebug,
  isDebugMode,
  filteredVariables,
  filteredFunctions,
  onEditorReady
}: FormulaInputProps) {
  const editorRef = useRef<any>(null);
  const updateRef = useRef<any>(null);
  const [errors, setErrors] = useState<Diagnostic[]>([]);

  const customLinter = linter((view: EditorView) => {
    const code = view.state.doc.toString();
    const diagnostics = validateFormula(code);
    return diagnostics;
  });

  //插入内容到指定位置，使用[[${id}.${label}]]的格式
  /**
   * 在编辑器的当前光标位置插入指定文本内容。
   * @param {string} text - 要插入的文本内容
   * @param {string} type - 文本类型，可选，默认值为 'text'
   * @param {number} position - 插入位置，可选，默认值为当前光标位置
   *
   */
  const insertAtPosition = useCallback((text: string, type?: string, position?: number) => {
    // 获取编辑器实例
    if (!updateRef.current || !updateRef.current.view) {
      console.warn('编辑器实例未准备好');
      return;
    }
    const view = updateRef.current.view;
    const state = view.state;
    const [range] = state?.selection?.ranges || [];

    // 根据类型格式化插入文本
    let insertText = text;
    if (type === 'var') {
      // 如果已经是[[...]]格式，不再重复添加
      if (!text.startsWith('[[') && !text.endsWith(']]')) {
        insertText = `[[${text}]]`;
      }
    } else if (type === 'fn') {
      // 确保函数格式正确并包含括号
      if (!text.startsWith('{{')) {
        // 提取函数名（去掉可能的括号）
        const funcName = text.includes('()') ? text.replace('()', '') : text;
        insertText = `{{${funcName}}}()`;
      } else if (!text.includes('()')) {
        // 如果已经有{{}}但没有括号，添加括号
        insertText = `${text}()`;
      }
    }

    // 确定插入位置
    let insertFrom = range?.from || 0;
    let insertTo = range?.to || insertFrom;

    // 如果指定了位置参数，则使用指定位置
    if (typeof position === 'number' && position >= 0 && position <= state.doc.length) {
      insertFrom = position;
      insertTo = position;
    }

    // 确定光标位置
    let cursorPosition = insertFrom + insertText.length;

    // 如果是函数类型，确保光标位于括号中间
    if (type === 'fn') {
      // 在最终的插入文本中查找左括号位置
      const leftBracketPos = insertText.indexOf('(');
      if (leftBracketPos !== -1) {
        // 将光标设置在括号中间
        cursorPosition = insertFrom + leftBracketPos + 1;
      }
    }
    view.dispatch({
      changes: {
        from: insertFrom,
        to: insertTo,
        insert: insertText
      },
      selection: {
        anchor: cursorPosition
      }
    });
    // 聚焦并插入文本
    view.focus();
  }, []);

  /**
   * 检查公式的语法是否正确。
   * @param {string} formula - 要检查的公式字符串
   * @returns {FormulaError[]} - 检查到的语法错误列表
   */
  const checkFormulaSyntax = useCallback((formula: string) => {
    const newErrors: FormulaError[] = [];

    // 检查括号匹配
    const bracketStack: { char: string; pos: number }[] = [];
    for (let i = 0; i < formula.length; i++) {
      const char = formula[i];
      if (char === '(' || char === '[' || char === '{') {
        bracketStack.push({ char, pos: i });
      } else if (char === ')' || char === ']' || char === '}') {
        const openChar = bracketStack.pop();
        if (
          !openChar ||
          (char === ')' && openChar.char !== '(') ||
          (char === ']' && openChar.char !== '[') ||
          (char === '}' && openChar.char !== '{')
        ) {
          newErrors.push({
            from: i,
            to: i + 1,
            message: '括号不匹配',
            severity: 'error'
          });
        }
      }
    }

    // 检查未闭合的括号
    bracketStack.forEach((bracket) => {
      newErrors.push({
        from: bracket.pos,
        to: bracket.pos + 1,
        message: '缺少闭合括号',
        severity: 'error'
      });
    });

    // 检查不支持的函数
    const unsupportedFunctions = ['sum'];
    unsupportedFunctions.forEach((funcName) => {
      const regex = new RegExp(`\\b${funcName}\\s*\\(`, 'g');
      let match;
      while ((match = regex.exec(formula)) !== null) {
        newErrors.push({
          from: match.index,
          to: match.index + funcName.length,
          message: `不支持${funcName}函数`,
          severity: 'error'
        });
      }
    });

    setErrors(newErrors);
  }, []);

  /**
   * 监听公式变化，检查语法错误。
   * 当公式值发生变化时，调用 checkFormulaSyntax 函数检查语法错误。
   */
  useEffect(() => {
    checkFormulaSyntax(value);
    const diagnostics = validateFormula(value);
    if (diagnostics.length > 0) {
      setErrors(diagnostics);
    }
  }, [value, checkFormulaSyntax]);

  /**
   * 监听编辑器就绪事件，将 insertAtPosition 函数传递给父组件。
   * 当 onEditorReady 回调函数存在时，将 insertAtPosition 函数作为参数传递给它。
   */
  useEffect(() => {
    if (onEditorReady) {
      onEditorReady({
        insertAtPosition: (text: string, type?: string, position?: number) => {
          insertAtPosition(text, type, position);
        }
      });
    }
  }, [onEditorReady, insertAtPosition]);

  /**
   * 处理复制按钮点击事件。
   * 复制当前公式和相关变量数据到剪贴板。
   */
  const handleCopy = useCallback(() => {
    // 复制公式和变量数据
    const copyData = {
      formula: value,
      variables: filteredVariables,
      functions: filteredFunctions
    };

    // 将数据转换为 JSON 字符串并复制到剪贴板
    const copyText = JSON.stringify(copyData);
    navigator.clipboard
      .writeText(copyText)
      .then(() => {
        onCopy();
      })
      .catch((err) => {
        console.error('复制失败:', err);
        // 降级处理：使用传统复制方法
        const textArea = document.createElement('textarea');
        textArea.value = copyText;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
        onCopy();
      });
  }, [value, filteredVariables, filteredFunctions, onCopy]);

  /**
   * 处理粘贴按钮点击事件。
   * 从剪贴板粘贴数据到当前光标位置。
   * @param {ClipboardEvent} event - 粘贴事件对象
   */
  const handlePaste = useCallback(
    (event: ClipboardEvent) => {
      try {
        const clipboardData = event.clipboardData?.getData('text');
        if (clipboardData) {
          const parsedData = JSON.parse(clipboardData);
          if (parsedData.formula) {
            // 在光标位置插入粘贴的公式
            insertAtPosition(parsedData.formula);
            event.preventDefault();
          }
        }
      } catch {
        // 如果不是 JSON 格式，按普通文本处理
        const clipboardData = event.clipboardData?.getData('text');
        if (clipboardData) {
          insertAtPosition(clipboardData);
          event.preventDefault();
        }
      }
    },
    [insertAtPosition]
  );

  /**
   * 监听粘贴事件，处理粘贴按钮点击事件。
   * 当粘贴事件发生时，调用 handlePaste 函数处理粘贴数据。
   */
  useEffect(() => {
    const editorElement = editorRef.current?.dom;
    if (editorElement) {
      editorElement.addEventListener('paste', handlePaste);
      return () => {
        editorElement.removeEventListener('paste', handlePaste);
      };
    }
  }, [handlePaste]);

  // 自定义扩展
  const extensions = [
    lintGutter(), //左侧错误标记
    customLinter,
    //调试模式只读
    isDebugMode ? EditorView.editable.of(false) : EditorView.editable.of(true),
    //设置首行-显示单行文本和两个按钮
    defaultExtenstion(handleCopy, onDebug, value, fieldName),
    EditorView.updateListener.of((update) => {
      updateRef.current = update;
    }),
    EditorView.lineWrapping,
    //覆盖codemirror原本的样式
    EditorView.baseTheme({
      '.cm-gutterElement': { display: 'none' },
      '.cm-content': { padding: 0 },
      '.cm-gutters.cm-gutters-before': { borderRightWidth: 0 }
    })
  ];

  return (
    <div className={styles.formulaInput}>
      <CodeMirror
        ref={editorRef}
        value={value}
        onChange={onChange}
        height="200px"
        placeholder="请输入公式或从左侧选择字段和函数"
        className={styles.editor}
        extensions={[placeholdersPlugin(), ...extensions]}
      />
      {/* 错误提示 */}
      {errors.length > 0 && (
        <div className={styles.errorSection}>
          {errors.map((error, index) => (
            <Alert key={index} type="error" content={error.message} showIcon className={styles.errorAlert} />
          ))}
        </div>
      )}
    </div>
  );
}
