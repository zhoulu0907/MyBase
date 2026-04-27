import React, { useRef, useCallback } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { basicSetup } from 'codemirror';
import { EditorView, keymap, placeholder as placeholderExtension, type ViewUpdate } from '@codemirror/view';
import { defaultKeymap } from '@codemirror/commands';
import { EditorState } from '@codemirror/state';
import styles from './index.module.less';

interface CodeMirrorEditorProps {
  value: string;
  onChange: (value: string) => void;
  theme?: 'light' | 'dark';
  readOnly?: boolean;
  placeholder?: string;
  className?: string;
  onCursorChange?: (position: { line: number; column: number }) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  insertText?: string;
  onInsertText?: () => void;
}

/**
 * 使用CodeMirror实现的代码编辑器组件
 */
const CodeMirrorEditor: React.FC<CodeMirrorEditorProps> = ({
  value,
  onChange,
  readOnly = false,
  placeholder = '请输入代码...',
  className = '',
  onCursorChange,
  onBlur,
  onFocus,
  insertText = '插入的文案',
  onInsertText
}) => {
  const editorRef = useRef<HTMLDivElement>(null);

  // 处理编辑器内容变化
  const handleChange = (value: string) => {
    onChange(value);
  };

  // 插入文本到编辑器
  const insertTextAtCursor = useCallback(() => {
    if (!editorRef.current || !insertText) return;

    // 获取编辑器视图
    const view = EditorView.findFromDOM(editorRef.current);
    if (!view) return;

    const state = view.state;
    const doc = state.doc.toString();
    const selection = state.selection.main;

    // 检查是否存在有效光标位置
    if (selection && typeof selection.head === 'number') {
      const cursorPos = selection.head;

      // 在光标位置插入文本
      const newDoc = doc.slice(0, cursorPos) + insertText + doc.slice(cursorPos);
      onChange(newDoc);

      // 打印插入信息
      const line = state.doc.lineAt(cursorPos);
      console.log(`在第${line.number}行插入文本`);

      // 通知外部插入事件
      if (onInsertText) {
        onInsertText();
      }
    } else {
      // 没有光标或光标位置无效，添加到文本末尾
      onChange(doc + insertText);

      // 打印插入信息
      console.log('在文本末尾插入文本（光标不存在）');

      // 通知外部插入事件
      if (onInsertText) {
        onInsertText();
      }
    }
  }, [insertText, onChange, onInsertText]);

  const handleFocusChange = (update: ViewUpdate) => {
    const hasFocus = update.view.hasFocus;

    if (hasFocus) {
      console.log('编辑器获得焦点');
      // 如果提供了onFocus回调，则调用它
      if (onFocus) {
        onFocus();
      }
    } else {
      console.log('编辑器失去焦点');
      // 如果提供了onBlur回调，则调用它
      if (onBlur) {
        onBlur();
      }
    }
  };
  const handleSelectionSetChange = (update: ViewUpdate) => {
    const selection = update.state.selection.main;

    // 检查是否存在有效光标位置
    if (selection && typeof selection.head === 'number') {
      const cursor = selection.head;
      const line = update.state.doc.lineAt(cursor);
      const position = {
        line: line.number,
        column: cursor - line.from + 1
      };

      // 判断是否有文本被选中（与光标位置相区分）
      const hasSelection = !selection.empty;

      // 打印光标信息
      if (hasSelection) {
        console.log('有文本被选中，光标位置:', position);
      } else {
        console.log('光标位置变化:', position);
      }

      // 只有当onCursorChange存在时才调用它
      if (onCursorChange) {
        onCursorChange(position);
      }
    } else {
      // 没有有效光标时，重置光标位置
      console.log('没有光标存在');
    }
  };

  // 自定义扩展
  const extensions = [
    basicSetup,
    keymap.of(defaultKeymap),
    EditorView.updateListener.of((update) => {
      if (update.docChanged) {
        handleChange(update.state.doc.toString());
      }

      // 监听焦点变化事件
      if (update.focusChanged) {
        handleFocusChange(update);
      }

      // 监听光标位置变化和光标是否存在
      if (update.selectionSet) {
        handleSelectionSetChange(update);
      }
    }),
    EditorView.lineWrapping,
    placeholderExtension(placeholder)
  ];

  // 添加只读模式
  if (readOnly) {
    extensions.push(EditorState.readOnly.of(true));
  }

  return (
    <div className={`${styles.editorWrapper} ${className}`}>
      <button
        onClick={insertTextAtCursor}
        disabled={readOnly}
        style={{
          marginBottom: '8px',
          padding: '6px 12px',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
          backgroundColor: '#fff',
          cursor: readOnly ? 'not-allowed' : 'pointer',
          fontSize: '14px',
          color: readOnly ? '#bfbfbf' : '#333'
        }}
      >
        插入文本
      </button>
      <div ref={editorRef}>
        <CodeMirror
          value={value}
          extensions={extensions}
          onChange={(val) => handleChange(val)}
          className={styles.editor}
        />
      </div>
    </div>
  );
};

export default CodeMirrorEditor;
export type { CodeMirrorEditorProps };
