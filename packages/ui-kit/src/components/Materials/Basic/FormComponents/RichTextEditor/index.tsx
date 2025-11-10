import '@wangeditor/editor/dist/css/style.css'; // 引入 css

import { Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import WangEditor from './editor';
import type { XRichTextConfig } from './schema';

const XRichText = memo((props: XRichTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig = '',
    verify,
    layout,
    runtime = true
  } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RICH_TEXT}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div dangerouslySetInnerHTML={{ __html: defaultValueConfig.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig.customValue : '' }}></div>
        ) : (
          <WangEditor
            runtime={runtime}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XRichText;
