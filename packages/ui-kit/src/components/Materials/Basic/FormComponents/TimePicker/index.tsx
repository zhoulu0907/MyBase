import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, TIME_FORMAT } from '../../../constants';
import '../index.css';
import type { XInputTimePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, defaultValue,dateType, verify, layout, labelColSpan = 0, runtime = true } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
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
        initialValue={defaultValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>{defaultValue || '--'}</div>
        ) : (
          <TimePicker
            format={TIME_FORMAT[dateType]}
            getPopupContainer={getPopupContainer}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XTimePicker;
