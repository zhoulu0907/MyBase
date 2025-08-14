import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Form, Input } from '@arco-design/web-react';
import { memo } from 'react';
import type { XInputTextAreaConfig } from './schema';

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig) => {
  const {
    label,
    dataField,
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
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
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
      <TextArea
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

export default XInputTextArea;
