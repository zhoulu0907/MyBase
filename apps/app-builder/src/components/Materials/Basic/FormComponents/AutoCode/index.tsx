import { memo } from 'react';
import type { XInputAutoCodeConfig } from './schema';
import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Form, Input } from '@arco-design/web-react';
import { v4 as uuidv4 } from 'uuid';

const XAutoCode = memo((props: XInputAutoCodeConfig) => {
  const { label, tooltip, status, required, align, layout, color, bgColor, labelColSpan = 0 } = props;

  const defaultValue = uuidv4();

  return (
    <Form.Item
      label={label}
      layout={layout}
      rules={[{ required }]}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      style={{
        margin: 0,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <Input
        readOnly={true}
        defaultValue={defaultValue}
        style={{
          width: '100%',
          color,
          textAlign: align,
          backgroundColor: bgColor
        }}
      />
    </Form.Item>
  );
});

export default XAutoCode;
