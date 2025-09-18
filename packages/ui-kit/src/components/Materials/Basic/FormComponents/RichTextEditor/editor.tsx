import React, { useState, useEffect, useMemo, useCallback, CSSProperties } from 'react';
import { Editor, Toolbar } from '@wangeditor/editor-for-react';
// 引入 wangEditor 的类型
import { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';

// ----------------- 类型定义 -----------------

// 1. 定义组件接收的 props 类型
interface WangEditorProps {
  value?: string; // Form.Item 注入的 value
  onChange?: (html: any) => void; // Form.Item 注入的 onChange
  runtime?: boolean;
  style?: CSSProperties;
}


// ----------------- 静态配置 -----------------

// 将静态配置移到外部，避免重渲染时重复创建
// 并为它们添加类型注解
const toolbarConfig: Partial<IToolbarConfig> = {};

const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入内容...',
  MENU_CONF: {},
  scroll: true
};


// ----------------- 封装的组件 -----------------

const WangEditorWrapper: React.FC<WangEditorProps> = ({ value = '', onChange, runtime, style }) => {
  // 2. 为 useState 添加类型注解
  const [editor, setEditor] = useState<IDomEditor | null>(null);

  // 使用 useCallback 确保 onChange 函数引用稳定
  const stableOnChange = useCallback((html: string) => {
    onChange?.(html);
  }, [onChange]);

  // 同步外部的 value 到编辑器
  useEffect(() => {
    // 确保 editor 实例已创建，并且外部 value 与内部 value 不一致时才更新
    if (editor && value !== editor.getHtml()) {
      editor.setHtml(value);
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