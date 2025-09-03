import { memo } from 'react';
import dayjs from 'dayjs';
import { DatePicker, Form } from '@arco-design/web-react';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDateRangePickerConfig } from './schema';
import './index.css';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig) => {
  const { label, status, tooltip, verify, layout, labelColSpan = 0, dateType, startTime, endTime, description } = props;

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];
  const validStartTime = startTime && dayjs(startTime);
  const validEndTime = endTime && dayjs(endTime);

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify.required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <DatePicker.RangePicker
        mode={currentDateType}
        defaultValue={[validStartTime, validEndTime]}
        showTime={dateType === DATE_VALUES[DATE_OPTIONS.FULL]}
        style={{ width: '100%' }}
      />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XDateRangePicker;
