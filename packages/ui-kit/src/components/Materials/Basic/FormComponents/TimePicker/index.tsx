import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputTimePickerConfig } from './schema';

const XTimePicker = memo((props: XInputTimePickerConfig) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <TimePicker defaultValue={defaultValue} style={{ width: '100%' }} />
    </Form.Item>
  );
});

export default XTimePicker;
