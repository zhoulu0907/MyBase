import { memo } from 'react';
import { nanoid } from 'nanoid';
import { DatePicker, Form } from '@arco-design/web-react';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XInputDatePickerConfig } from './schema';
import './index.css';

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig) => {
  const { label, dataField, tooltip, status, verify, dateType, layout, labelColSpan = 0, description } = props;

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <YearPicker style={{ width: '100%' }} />;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <MonthPicker style={{ width: '100%' }} />;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <DatePicker style={{ width: '100%' }} />;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <DatePicker showTime style={{ width: '100%' }} />;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} />;
    }
  };

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`}
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
      {renderDatePicker()}
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XDatePicker;
