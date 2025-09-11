import { memo } from 'react';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { DatePicker, Form } from '@arco-design/web-react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDateRangePickerConfig } from './schema';
import '../index.css';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig & { runtime?: boolean }) => {
  const { label, dataField, status, tooltip, verify, layout, defaultValue, labelColSpan = 0, dateType, startTime, endTime, runtime = true } = props;

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];
  const validStartTime = startTime && dayjs(startTime);
  const validEndTime = endTime && dayjs(endTime);

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`}
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
            <DatePicker.RangePicker
              mode={currentDateType}
              defaultValue={[validStartTime, validEndTime]}
              showTime={dateType === DATE_VALUES[DATE_OPTIONS.FULL]}
              style={{
                width: '100%',
                pointerEvents: runtime ? 'unset' : 'none'
              }}
            />
        }
      </Form.Item>
    </div>
  );
});

export default XDateRangePicker;
