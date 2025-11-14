import { DatePicker, Form } from '@arco-design/web-react';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, DATE_FORMAT, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
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
    startDefaultValueConfig,
    endDefaultValueConfig,
    dateType,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={[
          startdefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? startdefaultValueConfig?.customValue : '',
          enddefaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? enddefaultValueConfig?.customValue : ''
        ]}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {/* todo */}
            {/* {startTime} - {endTime} */}
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
