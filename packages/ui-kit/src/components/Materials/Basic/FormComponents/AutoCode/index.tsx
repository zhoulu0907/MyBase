import { Form, Input } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XautoCodeConfig } from './schema';

const XautoCode = memo((props: XautoCodeConfig) => {
  const {
    label,
    tooltip,
    placeholder,
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
      label={label.display && label.text}
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

export default XautoCode;
