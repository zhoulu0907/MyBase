import { Cell, DatePicker } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDatePickerConfig } from './schema';
import dayjs from 'dayjs';

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
    const getPopupContainer = (node?: HTMLElement): HTMLElement => {
      return (
        (node?.closest('.arco-form-item') as HTMLElement) ||
        node?.parentNode as HTMLElement ||
        document.body
      );
    };

    let mode: string | undefined;

    switch (currentDateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        mode = 'year';
        break;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        mode = 'month';
        break;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        mode = 'date';
        break;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        mode = 'date';
        break;
      default:
        mode = 'date';
    }

    const onPickerChange = (timestamp: number | [number, number]) => {
      // setPickerCurrentTs(timestamp);
    }


    return (
      <>
        <Cell
          showArrow
          label={label.display && label.text}
          // onClick={() => {setPickerVisible(true);}} // 预览或运行态
        />
        <DatePicker
          currentTs={pickerCurrentTs}
          mode={mode as any}
          getContainer={getPopupContainer}
          visible={pickerVisible}
          onHide={() => setPickerVisible(false)}
          onOk={() => setPickerVisible(false)}
          onChange={onPickerChange}
        />
      </>
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
