import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { DatePicker, Form } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDateTimePickerConfig } from './schema';
import '../index.css';

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

  // const [fieldId, setFieldId] = useState('');

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  // useEffect(() => {
  //   if (dataField.length > 0) {
  //     setFieldId(dataField[dataField.length - 1]);
  //   }
  // }, [dataField]);

  return (
    <Form.Item
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      required={verify?.required}
      style={{
        textAlign: 'right'
      }}
    >
      {!runtime || detailMode ? (
        <div>{defaultValue || '--'}</div>
      ) : (
        <DatePicker
          title={label.text}
          maskClosable
          typeArr={['year', 'month', 'date', 'hour', 'minute']}
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
      )}
    </Form.Item>
  );
});

export default XDateTimePicker;
