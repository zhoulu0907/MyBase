import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Form, Input } from '@arco-design/web-react';
import { memo } from 'react';
import { type XInputTextConfig } from './schema';

const XInputText = memo((props: XInputTextConfig) => {
  const {
    label,
    placeholder,
    tooltip,
    status,
    defaultValue,
    required,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0
  } = props;

  return (
    <Form.Item
      label={label}
      layout={layout}
      labelCol={{
        span: labelColSpan
      }}
      tooltip={tooltip}
      wrapperCol={{ span: 24 - labelColSpan }}
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Input
        readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
        defaultValue={defaultValue}
        placeholder={placeholder}
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

export default XInputText;
