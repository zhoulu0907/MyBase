import { Cell, DatePicker } from '@arco-design/mobile-react';
import { memo, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputTimePickerConfig } from './schema';
import dayjs from 'dayjs';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, runtime = true } = props;
  const [pickerVisible, setPickerVisible] = useState(false);
  const [pickerCurrentTs, setPickerCurrentTs] = useState(defaultValue ? dayjs(defaultValue).valueOf() : new Date().getTime());

  return (
    <div className="formWrapper">
      <Cell
        showArrow
        label={label.display && label.text}
        // onClick={() => {setPickerVisible(true);}}
      >
        <DatePicker
          currentTs={pickerCurrentTs}
          mode={"time"}
          visible={pickerVisible}
          onHide={() => setPickerVisible(false)}
          onOk={() => setPickerVisible(false)}
          contentStyle={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Cell>
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
