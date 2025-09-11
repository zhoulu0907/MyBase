import { nanoid } from 'nanoid';
import { memo } from 'react';
import { DatePicker, Form } from '@arco-design/web-react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDatePickerConfig } from './schema';
import '../index.css';

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, defaultValue, verify, dateType, layout, labelColSpan = 0, runtime = true } = props;

  // 确保 dateType 有默认值，避免 Form.Item 中没有元素
  const currentDateType = dateType || DATE_VALUES[DATE_OPTIONS.DATE];

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    const styles = {
      width: '100%',
      pointerEvents: (runtime ? 'auto' : 'none') as React.CSSProperties['pointerEvents']
    }
    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <YearPicker style={styles} />;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <MonthPicker style={styles} />;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <DatePicker style={styles} />;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <DatePicker showTime style={styles} />;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} />;
    }
  };

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`}
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
            renderDatePicker()
        }
      </Form.Item>
    </div>
  );
});

export default XDatePicker;
