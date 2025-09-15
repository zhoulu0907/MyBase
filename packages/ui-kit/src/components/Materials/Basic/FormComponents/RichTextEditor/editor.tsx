import React, { useState, useEffect, CSSProperties } from 'react';
import { Editor, Toolbar } from '@wangeditor/editor-for-react';
// 引入 wangEditor 的类型
import { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';

// ----------------- 类型定义 -----------------

// 1. 定义组件接收的 props 类型
interface WangEditorProps {
  value?: string; // Form.Item 注入的 value
  onChange?: (html: any) => void; // Form.Item 注入的 onChange
  // 你也可以添加其他自定义 props，例如 height
  height?: number;
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
};


// ----------------- 封装的组件 -----------------

const WangEditorWrapper: React.FC<WangEditorProps> = ({ value = '', onChange, height = 350, runtime, style }) => {
  // 2. 为 useState 添加类型注解
  const [editor, setEditor] = useState<IDomEditor | null>(null);

  // 同步外部的 value 到编辑器
  useEffect(() => {
    // 确保 editor 实例已创建，并且外部 value 与内部 value 不一致时才更新
    if (editor && value !== editor.getHtml()) {
      editor.setHtml(value);
    }
  }, [value, editor]);

  useEffect(() => {
    runtime ? editor?.enable() : editor?.disable();
  }, [runtime]);

  // 组件销毁时销毁 editor 实例
  useEffect(() => {
    return () => {
      if (editor == null) return;
      editor.destroy();
      setEditor(null);
    };
  }, [editor]);

  // 编辑器内容变化时的回调
  const handleEditorChange = (currentEditor: IDomEditor) => {
    const newHtml = currentEditor.getHtml();
    // 只有当内容真正变化时才调用 onChange，并使用可选链操作符确保 onChange 存在
    if (newHtml !== value) {
      onChange?.(newHtml);
    }
  };

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
        style={{ minHeight: `${height}px`, overflowY: 'auto' }}
      />
    </div>
  );
};

export default WangEditorWrapper;