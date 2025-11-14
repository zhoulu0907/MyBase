import { Cell, DatePicker } from '@arco-design/mobile-react';
import { memo, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputTimePickerConfig } from './schema';
import dayjs from 'dayjs';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, runtime = true } = props;
  const [pickerCurrentTs, setPickerCurrentTs] = useState(defaultValue ? dayjs(defaultValue).valueOf() : new Date().getTime());

  const onPickerChange = (timestamp: number | [number, number]) => {
    setPickerCurrentTs(timestamp);
  }

  return (
    <div className="formWrapper">
      <DatePicker
        mode={"time"}
        title={label.text}
        currentTs={pickerCurrentTs}
        contentStyle={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        renderLinkedContainer={(_, data) => (
          <Cell
            label={label.display && label.text}
            showArrow
            bordered={false}
          >{dayjs(pickerCurrentTs).format('HH:mm:ss')}</Cell>
        )}
        formatter={(value, type) => {
          if (type === 'hour') {
            return `${value}时`;
          } else if (type === 'minute') {
            return `${value}分`;
          } else if (type === 'second') {
            return `${value}秒`;
          }
        }}
        onChange={onPickerChange}
      />
      {/* <Form.Item
        label={label.display && label.text}
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>{defaultValue || '--'}</div>
        ) : (
          <TimePicker
            defaultValue={defaultValue}
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

export default XTimePicker;
