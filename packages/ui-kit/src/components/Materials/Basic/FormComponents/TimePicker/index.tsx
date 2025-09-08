import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputTimePickerConfig } from './schema';
import '../index.css';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description, runtime = true } = props;

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
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
      }}
    >
      <TimePicker defaultValue={defaultValue} style={{ width: '100%' }} />
    </Form.Item>
  );
});

export default XTimePicker;
