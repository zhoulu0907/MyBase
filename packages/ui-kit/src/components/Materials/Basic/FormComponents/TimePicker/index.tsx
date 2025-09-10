import { memo } from 'react';
import { Form, TimePicker } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputTimePickerConfig } from './schema';
import '../index.css';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, runtime = true } = props;

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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? <div>{defaultValue || '--'}</div> :
            <TimePicker defaultValue={defaultValue} style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }} />
        }
      </Form.Item>
    </div>
  );
});

export default XTimePicker;
