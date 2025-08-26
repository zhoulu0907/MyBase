import { Form } from '@arco-design/web-react';
import { Plate, PlateContent, usePlateEditor } from 'platejs/react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';

import type { XRichTextConfig } from './schema';

const XRichText = memo((props: XRichTextConfig) => {
  const editor = usePlateEditor();

  const { label, dataField, tooltip, status, /* defaultValue, */ required, layout, labelColSpan = 0 } = props;

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required }]}
      style={{
        margin: 0,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <Plate
        editor={editor}
        onChange={({ value }) => {
          // 将编辑器更改同步到表单
          console.log('content', value);
        }}
      >
        <PlateContent placeholder="在此输入..." />
      </Plate>
    </Form.Item>
  );
});

export default XRichText;
