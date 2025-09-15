import { Button, Space, Alert } from '@arco-design/web-react';
import { IconCopy, IconBug } from '@arco-design/web-react/icon';
import { useCallback, useRef, useEffect, useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { Decoration, EditorView, MatchDecorator, ViewPlugin, ViewUpdate } from '@codemirror/view';
import type { Variable, FunctionItem } from '../index';
import { placeholdersPlugin } from '../utils/placeholders';
import styles from './FormulaInput.module.less';

interface FormulaInputProps {
  value: string;
  onChange: (value: string) => void;
  onCopy: () => void;
  onDebug: () => void;
  filteredVariables: Variable[];
  filteredFunctions: FunctionItem[];
  onEditorReady?: (editor: { insertAtPosition: (text: string, type?: string, position?: number) => void }) => void;
}

interface FormulaError {
  message: string;
  from: number;
  to: number;
  severity: 'error' | 'warning';
}

export function FormulaInput({
  value,
  onChange,
  onCopy,
  onDebug,
  filteredVariables,
  filteredFunctions,
  onEditorReady
}: FormulaInputProps) {
  const editorRef = useRef<any>(null);
  const [errors, setErrors] = useState<FormulaError[]>([]);

  // 插入内容到指定位置, 使用[[${id}.${label}]]的格式
  const insertAtPosition = useCallback(
    (text: string, type?: string, position?: number) => {
      if (!editorRef.current) return;

      const editor = editorRef.current;
      const { view, state } = editor;
      const [range] = state?.selection?.ranges || [];
      console.log('editor', editor);

      // 根据类型插入不同格式的内容
      let insertText = text;
      // if (type === 'var') {
      //   // 变量插入为标签格式 [[字段名]]
      //   insertText = `[[${text}]]`;
      // } else if (type === 'fn') {
      //   // 函数插入为函数格式 {{函数名()}}
      //   insertText = `{{${text}}}`;
      // }

      // if (view.insertAtCursor) {
      //   // 如果有光标，在光标位置插入
      //   editor.insertAtCursor(insertText);
      // } else {
      //   // 否则在末尾插入
      //   onChange(value + insertText);
      // }
      view.focus();
      view.dispatch({
        changes: {
          from: range.from,
          to: range.to,
          insert: text
        },
        selection: {
          anchor: range.from + text.length
        }
      });
    },
    [onChange, value]
  );

  // 检查公式语法
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

  // 监听公式变化，检查语法
  useEffect(() => {
    checkFormulaSyntax(value);
  }, [value, checkFormulaSyntax]);

  // 通知父组件编辑器已就绪
  useEffect(() => {
    if (onEditorReady) {
      onEditorReady({
        insertAtPosition: (text: string, type?: string, position?: number) => {
          insertAtPosition(text, type, position);
        }
      });
    }
  }, [onEditorReady, insertAtPosition]);

  // 处理复制功能
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

  // 处理粘贴功能
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

  // 监听粘贴事件
  useEffect(() => {
    const editorElement = editorRef.current?.dom;
    if (editorElement) {
      editorElement.addEventListener('paste', handlePaste);
      return () => {
        editorElement.removeEventListener('paste', handlePaste);
      };
    }
  }, [handlePaste]);

  return (
    <div className={styles.formulaInput}>
      <div className={styles.label}>单行文本 =</div>
      <div className={styles.inputWrapper}>
        <CodeMirror
          ref={editorRef}
          value={value}
          onChange={onChange}
          height="200px"
          placeholder="请输入公式或从左侧选择字段和函数"
          className={styles.editor}
          extensions={[placeholdersPlugin()]}
        />
        <Space className={styles.actions}>
          <Button size="small" icon={<IconCopy />} onClick={handleCopy} className={styles.actionButton}>
            复制
          </Button>
          <Button size="small" icon={<IconBug />} onClick={onDebug} className={styles.actionButton}>
            调试
          </Button>
        </Space>
      </div>

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
