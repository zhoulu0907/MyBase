import '@wangeditor/editor/dist/css/style.css' // 引入 css

import { useState, useEffect, memo } from 'react'
import { Form } from '@arco-design/web-react';
import { Editor, Toolbar } from '@wangeditor/editor-for-react'
import { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor'
import { nanoid } from 'nanoid';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XRichTextConfig } from './schema';

import './index.css';

const XRichText = memo((props: XRichTextConfig) => {
  const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description } = props;

  // 编辑器内容
  const [html, setHtml] = useState<string>();
  const [editor, setEditor] = useState<IDomEditor | null>(null);

  // 工具栏配置
  const toolbarConfig: Partial<IToolbarConfig> = {
    // modalAppendToBody: true
  };

  // 编辑器配置
  const editorConfig: Partial<IEditorConfig> = {
    placeholder: '请输入内容...',
  };

  useEffect(() => {
    defaultValue && setHtml(defaultValue);
  }, [defaultValue]);

  useEffect(() => {
    if (editor === null) return;
    if (status === STATUS_VALUES[STATUS_OPTIONS.READONLY]) {
      editor?.disable();
    } else {
      editor?.enable();
    }
  }, [editor, status]);

  useEffect(() => {
    return () => {
      if (editor === null) return;
      editor.destroy();
      setEditor(null);
    }
  }, [editor]);

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RICH_TEXT}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }]}
      style={{
        margin: 0,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <div style={{ border: '1px solid #ccc', zIndex: 100 }}>
        <Toolbar
          editor={editor}
          defaultConfig={toolbarConfig}
          mode="default"
          style={{ borderBottom: '1px solid #ccc' }}
        />
        <Editor
          defaultConfig={editorConfig}
          value={html}
          onCreated={setEditor}
          onChange={(editor) => setHtml(editor.getHtml())}
          mode="default"
          style={{ height: '300px', overflowY: 'hidden' }}
        />
      </div>
      {/* <div style={{ marginTop: '15px' }}>{html}</div> */}
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XRichText;
