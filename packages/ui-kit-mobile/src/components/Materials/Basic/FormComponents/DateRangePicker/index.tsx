import { Cell, DatePicker } from '@arco-design/mobile-react';
import dayjs from 'dayjs';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DATE_OPTIONS, DATE_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDateRangePickerConfig } from './schema';

const XDateRangePicker = memo((props: XInputDateRangePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
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

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');
  const [pickerCurrentTs, setPickerCurrentTs] = useState<number[]>(startTime && endTime ? [dayjs(startTime).valueOf(), dayjs(endTime).valueOf()] : []);

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  const currentDateType = (dateType !== DATE_VALUES[DATE_OPTIONS.FULL] && dateType) || DATE_VALUES[DATE_OPTIONS.DATE];
  const validStartTime = startTime && dayjs(startTime).valueOf() || Date.now();
  const validEndTime = endTime && dayjs(endTime).valueOf() || Date.now();

  const onPickerChange = (timestamp: number | [number, number]) => {
    setPickerCurrentTs(timestamp);
  }

  return (
    <div className="inputTextWrapper">
      <DatePicker
        mode={"date"}
        title={label.text}
        maskClosable
        currentTs={[validStartTime, validEndTime]}
        onChange={onPickerChange}
        renderLinkedContainer={(_, data) => (
          <Cell
            label={label.display && label.text}
            showArrow
            bordered={false}
          >{pickerCurrentTs.map(t => dayjs(t).format('YYYY/MM/DD')).join(' - ')}</Cell>
        )}
      />

      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0
            ? dataField[dataField.length - 1]
            : `${FORM_COMPONENT_TYPES.DATE_RANGE_PICKER}_${nanoid()}`
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
          <div>
            {startTime} - {endTime}
          </div>
        ) : (
          <DatePicker.RangePicker
            mode={currentDateType}
            defaultValue={[validStartTime, validEndTime]}
            showTime={dateType === DATE_VALUES[DATE_OPTIONS.FULL]}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XDateRangePicker;
