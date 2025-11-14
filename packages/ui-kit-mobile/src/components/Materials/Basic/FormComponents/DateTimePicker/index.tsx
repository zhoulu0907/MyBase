import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { Cell, DatePicker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDateTimePickerConfig } from './schema';
import dayjs from 'dayjs';

const XDateTimePicker = memo((props: XInputDateTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');
  const [pickerVisible, setPickerVisible] = useState(false);
  const [pickerCurrentTs, setPickerCurrentTs] = useState(defaultValue ? dayjs(defaultValue).valueOf() : new Date().getTime());

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Cell
        showArrow
        label={label.display && label.text}
        onClick={() => { setPickerVisible(true); }}
      />
      <DatePicker
        currentTs={pickerCurrentTs}
        typeArr={['year', 'month', 'date', 'hour', 'minute']}
        visible={pickerVisible}
        onHide={() => setPickerVisible(false)}
        onOk={() => setPickerVisible(false)}
      />

      {/* <Form.Item
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
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
      </Form.Item> */}
    </div>
  );
});

export default XDateTimePicker;
