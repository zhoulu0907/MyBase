import { memo } from 'react';
import { DatePicker, Form } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDateTimePickerConfig } from './schema';
import '../index.css';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description, runtime = true, } = props;

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
      <DatePicker showTime defaultValue={defaultValue} style={{ width: '100%' }} />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XDateTimePicker;
