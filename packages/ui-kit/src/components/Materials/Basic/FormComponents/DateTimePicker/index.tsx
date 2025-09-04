import { memo } from 'react';
import { DatePicker, Form } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDateTimePickerConfig } from './schema';
import './index.css';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig) => {
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
      <DatePicker showTime defaultValue={defaultValue} style={{ width: '100%' }} />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XDateTimePicker;
