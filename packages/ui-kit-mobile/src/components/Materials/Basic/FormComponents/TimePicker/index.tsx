import { DatePicker, Form } from '@arco-design/mobile-react';
import { memo, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputTimePickerConfig } from './schema';
import '../index.css';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, runtime = true, detailMode } = props;

  return (
    <Form.Item
      className="inputTextWrapper"
      field={''}
      label={label.display && label.text}
      required={verify?.required}
      style={{
        textAlign: 'right'
      }}
    >
      {!runtime || detailMode ? (
        <div>{defaultValue || '--'}</div>
      ) : (
        <DatePicker
          mode={"time"}
          title={label.text}
          maskClosable
          contentStyle={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
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

export default XTimePicker;
