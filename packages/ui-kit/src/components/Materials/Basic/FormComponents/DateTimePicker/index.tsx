import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputDateTimePickerConfig } from './schema';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig) => {
  const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, description } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`}
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
    </Form.Item>
  );
});

export default XDateTimePicker;
