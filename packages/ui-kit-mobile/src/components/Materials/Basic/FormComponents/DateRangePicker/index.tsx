import { DatePicker, Form } from '@arco-design/mobile-react';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDateRangePickerConfig } from './schema';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    cpName,
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

  // const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];
  const validStartTime = startTime && dayjs(startTime).valueOf() || Date.now();
  const validEndTime = endTime && dayjs(endTime).add(1, 'year').valueOf() || Date.now();

  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <DatePicker
        mode="date"
        title={label.text}
        maskClosable
        formatter={(value, type) => {
          const map = {
            year: '年',
            month: '月',
            date: '日',
            hour: '时',
            minute: '分',
            second: '秒',
          };
          return `${value}${map[type] || ''}`;
        }}
      />
    );
  };

  return (
    <Form.Item
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      initialValue={[validStartTime, validEndTime]}
      required={verify.required}
      style={{
        textAlign: 'right'
      }}
    >
      {!runtime || detailMode ? (
        <div>
          {startTime} - {endTime}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XDateRangePicker;
