import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { DatePicker, Form } from '@arco-design/web-react';
import { memo } from 'react';
import type { XInputDatePickerConfig } from './schema';

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig) => {
  const { label, tooltip, status, required, dateType, layout, labelColSpan = 0 } = props;

  return (
    <Form.Item
      label={label}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        span: labelColSpan
      }}
      wrapperCol={{ span: 24 - labelColSpan }}
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_YEAR] && <YearPicker style={{ width: '100%' }} />}
      {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_MONTH] && <MonthPicker style={{ width: '100%' }} />}
      {dateType === DATE_VALUES[DATE_OPTIONS.ONLY_DATE] && <DatePicker style={{ width: '100%' }} />}
      {dateType === DATE_VALUES[DATE_OPTIONS.FULL] && <DatePicker showTime style={{ width: '100%' }} />}
    </Form.Item>
  );
});

export default XDatePicker;
