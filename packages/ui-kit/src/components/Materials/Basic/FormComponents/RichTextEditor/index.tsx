import '@wangeditor/editor/dist/css/style.css'; // 引入 css

import { memo } from 'react';
import { Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import WangEditor from './editor';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XRichTextConfig } from './schema';
import '../index.css';

const XRichText = memo((props: XRichTextConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, defaultValue = '', verify, layout, labelColSpan = 0, runtime = true } = props;

  return (
    <div className='formWrapper'>
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
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? <div dangerouslySetInnerHTML={{ __html: defaultValue }}></div> : (
          <WangEditor runtime={runtime} style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }} />
        )}
      </Form.Item>
    </div>
  );
});

export default XRichText;
