import { DatePicker, Form } from '@arco-design/web-react';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, DATE_FORMAT, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDateRangePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    verify,
    layout,
    defaultValue,
    labelColSpan = 0,
    dateType,
    startTime,
    endTime,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];
  const validStartTime = startTime && dayjs(startTime);
  const validEndTime = endTime && dayjs(endTime);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0
            ? dataField[dataField.length - 1]
            : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`
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
        initialValue={[validStartTime, validEndTime]}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {startTime} - {endTime}
          </div>
        ) : (
          <DatePicker.RangePicker
            format={DATE_FORMAT[dateType]}
            mode={currentDateType}
            showTime={dateType === DATE_VALUES[DATE_OPTIONS.FULL]}
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

export default XDateRangePicker;
