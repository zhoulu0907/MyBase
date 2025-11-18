import { DatePicker, Form } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDatePickerConfig } from './schema';
import dayjs from 'dayjs';
import { ItemType } from '@arco-design/mobile-react/cjs/date-picker';

const XDatePicker = memo((props: XInputDatePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    cpName,
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    dateType,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

  const [pickerCurrentTs, setPickerCurrentTs] = useState(defaultValue ? dayjs(defaultValue).valueOf() : new Date().getTime());

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    let mode: ItemType[];
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        mode = ['year'];
        break;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        mode = ['year', 'month'];
        break;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        mode = ['year', 'month', 'date'];
        break;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        mode = ['year', 'month', 'month', 'hour', 'minute', 'second'];
        break;
      default:
        mode = ['date'];
    }

    const onPickerChange = (timestamp: number | [number, number]) => {
      setPickerCurrentTs(timestamp);
    }

    return (
      <DatePicker
        title={label.text}
        typeArr={mode}
        currentTs={pickerCurrentTs}
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
        onChange={onPickerChange}
      />
    )
  };

  return (
    <Form.Item
      className="inputTextWrapper"
      field={fieldId}
      label={label.display && label.text}
      required={verify.required}
      style={{ textAlign: 'right' }}
    >
      {!runtime || detailMode ? (
        <div>{defaultValue || '--'}</div>
      ) : (
        renderDatePicker()
      )}
    </Form.Item>
  );
});

export default XDatePicker;
