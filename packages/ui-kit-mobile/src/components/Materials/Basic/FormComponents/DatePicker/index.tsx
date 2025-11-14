import { Cell, DatePicker } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDatePickerConfig } from './schema';
import dayjs from 'dayjs';
import { ItemType } from '@arco-design/mobile-react/cjs/date-picker';

const XDatePicker = memo((props: XInputDatePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
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

  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    let mode: ItemType[];
    let formatterText = '';
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        mode = ['year'];
        formatterText = 'YYYY';
        break;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        mode = ['year', 'month'];
        formatterText = 'YYYY/MM';
        break;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        mode = ['year', 'month', 'date'];
        formatterText = 'YYYY/MM/DD';
        break;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        mode = ['year', 'month', 'month', 'hour', 'minute', 'second'];
        formatterText = 'YYYY/MM/DD HH:mm:ss';
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
        renderLinkedContainer={(_, data) => (
          <Cell
            label={label.display && label.text}
            showArrow
            bordered={false}
          >{dayjs(pickerCurrentTs).format(formatterText)}</Cell>
        )}
        formatter={(value, type) => {
          if (type === 'year') {
            return `${value}年`;
          } else if (type === 'month') {
            return `${value}月`;
          } else if (type === 'date') {
            return `${value}日`;
          } else if (type === 'hour') {
            return `${value}时`;
          } else if (type === 'minute') {
            return `${value}分`;
          } else if (type === 'second') {
            return `${value}秒`;
          }
        }}
        onChange={onPickerChange}
      />
    )
  };

  return (
    <div className="formWrapper">
      {renderDatePicker()}
      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`
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
          renderDatePicker()
        )}
      </Form.Item> */}
    </div>
  );
});

export default XDatePicker;
