import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputTimePickerConfig } from './schema';
import '../index.css';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description, runtime = true } = props;

  return (
    <div className='formWrapper'>
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
        }}
      >
        <TimePicker defaultValue={defaultValue} style={{ width: '100%' }} />
      </Form.Item>
      <div className='description showEllipsis' style={{marginLeft: labelColSpan}}>{description}</div>
    </div>
  );
});

export default XTimePicker;
