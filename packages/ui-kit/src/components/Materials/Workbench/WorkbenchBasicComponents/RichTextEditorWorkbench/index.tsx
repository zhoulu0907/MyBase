import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { nanoid } from 'nanoid';
import { Form } from '@arco-design/web-react';
import { memo } from 'react';
import '../index.css';
import WangEditor from './editor';
import type { XRichTextConfig } from './schema';

const XRichText = memo((props: XRichTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    id,
    label,
    runtime = true,
    WbRichTextContent = '',
    WbColor = '#FFFFFF',
    status
  } = props;

  const fieldName = `WbRichTextContent_${id || 'field'}`;
  const displayValue = WbRichTextContent || '';

  const containerStyle = {
    width: '100%',
    backgroundColor: WbColor,
    padding: '10px',
    borderRadius: '4px',
    boxSizing: 'border-box',
    minHeight: '100px',
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span>{label.text}</span>
        }
        field={runtime ? fieldName : `WbRichTextContent_${nanoid()}`}
        initialValue={displayValue}
        wrapperCol={{ style: { flex: 1 } }}
        layout="vertical"
      >
        {/* <WangEditor
          runtime={runtime}
          value={runtime ? undefined : displayValue}
          style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        /> */}
        {displayValue ? 
          <div dangerouslySetInnerHTML={{ __html: displayValue }} style={containerStyle}></div> : 
          <div style={{...containerStyle, color: '#86909C'}}>请输入</div>}
      </Form.Item>
    </div>
  );
});

export default XRichText;
