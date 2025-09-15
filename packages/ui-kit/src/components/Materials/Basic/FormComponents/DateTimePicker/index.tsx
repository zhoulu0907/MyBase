import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDateTimePickerConfig } from './schema';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, runtime = true } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`
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
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>{defaultValue || '--'}</div>
        ) : (
          <DatePicker
            showTime
            defaultValue={defaultValue}
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

export default XDateTimePicker;
