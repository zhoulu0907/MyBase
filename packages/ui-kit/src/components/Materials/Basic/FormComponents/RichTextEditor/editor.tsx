import React, { useState, useEffect, useMemo, useCallback, CSSProperties, useRef } from 'react';
import { Editor, Toolbar } from '@wangeditor/editor-for-react';
// 引入 wangEditor 的类型
import { IDomEditor, IEditorConfig, IToolbarConfig, SlateTransforms } from '@wangeditor/editor';
import { DEFAULT_VALUE_TYPES } from '@/components/Materials/constants';
import { TSelectDefaultType, TTextDefaultType } from '@/components/Materials/types';
import { TAlignSelectKeyType } from '@/components/Materials/common';

// ----------------- 类型定义 -----------------

// 1. 定义组件接收的 props 类型
interface WangEditorProps {
  value?: string; // Form.Item 注入的 value
  onChange?: (html: any) => void; // Form.Item 注入的 onChange
  runtime?: boolean;
  placeholder?: TTextDefaultType;
  style?: CSSProperties;
  align?: TSelectDefaultType<TAlignSelectKeyType>;
  defaultValueConfig?: any;
}


// ----------------- 静态配置 -----------------
const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入内容...',
  MENU_CONF: {},
  scroll: true
};

// 将静态配置移到外部，避免重渲染时重复创建
// 并为它们添加类型注解
const toolbarConfig: Partial<IToolbarConfig> = {};

// ----------------- 封装的组件 -----------------

const WangEditorWrapper: React.FC<WangEditorProps> = ({ value = '', onChange, align, runtime, placeholder, defaultValueConfig, style }) => {
  const isSyncingFromConfigRef = useRef(false);

  // 2. 为 useState 添加类型注解
  const [editor, setEditor] = useState<IDomEditor | null>(null);

  useEffect(() => {
    if (editor && placeholder) {
      editor.getConfig().placeholder = placeholder;
      const $placeholder = editor.getEditableContainer().querySelector('.w-e-text-placeholder');
      if ($placeholder) {
        $placeholder.innerHTML = placeholder;
        // placeholder 的对齐需要跟随正文对齐
        if (align) {
          ($placeholder as HTMLElement).style.textAlign = String(align);
        }
      }
    }
  }, [placeholder, editor, align]);

  useEffect(() => {
    const customValue =
      defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : '';
    if (!editor) return;
    if (customValue === undefined || customValue === null) return;
    if (customValue === editor.getHtml()) return;

    // 记录当前聚焦元素，避免在设计态下修改默认值时编辑器抢焦点导致右侧输入框失焦
    const activeElement = (typeof document !== 'undefined'
      ? (document.activeElement as HTMLElement | null)
      : null);

    isSyncingFromConfigRef.current = true;
    
    // 延迟到下一帧再 setHtml，避免 Slate 内部 DOM 尚未完全 ready 时出现
    // “Cannot resolve a DOM node from Slate node: { text: '' }” 等错误
    setTimeout(() => {
      if (!editor) {
        isSyncingFromConfigRef.current = false;
        return;
      }
      try {
        editor.setHtml(customValue);
        // 默认值设置完后，如果有对齐要求，顺便应用一次
        if (align) {
          try {
            editor.selectAll();
            SlateTransforms.setNodes(editor, { textAlign: align }, { mode: 'all' });
          } catch { }
        }
      } catch {
        // 避免 setHtml 异常导致页面白屏
      } finally {
        isSyncingFromConfigRef.current = false;
      }
    }, 0);

    if (activeElement && typeof activeElement.focus === 'function') {
      // 异步恢复焦点，确保在编辑器内部处理完成后执行
      setTimeout(() => {
        try {
          activeElement.focus();
        } catch {
          // ignore
        }
      }, 0);
    }
  }, [defaultValueConfig, editor, align]);

  useEffect(() => {
    if (!editor || !align) return;
    try {
      editor.selectAll();
      SlateTransforms.setNodes(editor, { textAlign: align }, { mode: 'all' });
    } catch { }
  }, [align, editor]);

  // 使用 useCallback 确保 onChange 函数引用稳定
  const stableOnChange = useCallback((html: string) => {
    onChange?.(html);
  }, [onChange]);

  // 同步外部的 value 到编辑器
  useEffect(() => {
    if (!editor) return;
    // 如果外部 value 是空字符串/未定义，认为走“默认值配置”逻辑，不再用空值覆盖 defaultValueConfig.customValue
    if (value === '' || value === undefined || value === null) return;
    if (value === editor.getHtml()) return;

    isSyncingFromConfigRef.current = true;
    try {
      editor.setHtml(value);
    } catch {
      // 避免 setHtml 异常导致页面白屏
    } finally {
      isSyncingFromConfigRef.current = false;
    }
  }, [value, editor]);

  // 控制编辑器的启用/禁用状态
  useEffect(() => {
    if (editor) {
      runtime ? editor.enable() : editor.disable();
    }
  }, [runtime, editor]);

  // 组件销毁时销毁 editor 实例
  useEffect(() => {
    return () => {
      if (editor == null) return;
      console.debug('销毁编辑器');
      editor.destroy();
      setEditor(null);
    };
  }, [editor]);

  // 使用 useMemo 缓存编辑器变化的回调函数
  const handleEditorChange = useMemo(() => {
    return (currentEditor: IDomEditor) => {
      if (isSyncingFromConfigRef.current) {
        isSyncingFromConfigRef.current = false;
        return;
      }
      const newHtml = currentEditor.getHtml();
      // 只有当内容真正变化时才调用 onChange
      if (newHtml !== value) {
        stableOnChange(newHtml);
      }
    };
  }, [value, stableOnChange]);

  return (
    <div style={{ border: '1px solid #ccc', zIndex: 100, ...style }}>
      <Toolbar
        editor={editor}
        defaultConfig={toolbarConfig}
        mode="default"
        style={{ borderBottom: '1px solid #ccc' }}
      />
      <Editor
        defaultConfig={editorConfig}
        onCreated={setEditor}
        onChange={handleEditorChange}
        mode="default"
        style={{ height: 400, overflowY: 'auto' }}
      />
    </div>
  );
};

export default WangEditorWrapper;