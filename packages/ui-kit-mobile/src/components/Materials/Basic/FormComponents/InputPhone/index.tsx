import { Input } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputPhoneConfig } from './schema';

const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="inputTextWrapper">
      <Input
        label={label.display && label.text}
        type="tel"
        defaultValue={defaultValue}
        placeholder={placeholder}
        inputStyle={{ textAlign: align }}
        style={{
          width: '100%',
          backgroundColor: bgColor,
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />

      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_PHONE}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          { required: verify?.required },
          {
            match: /^1[3-9]\d{9}$/,
            message: '请输入有效的11位中国大陆手机号'
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
            defaultValue={defaultValue}
            style={{
              width: '100%',
              color,
              textAlign: align,
              backgroundColor: bgColor,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            placeholder={placeholder}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XInputPhone;
