import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputPhoneConfig } from './schema';

const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean }) => {
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
    description,
    runtime = true
  } = props;

  const [value, setValue] = useState('');
  const [InputStatus, setInputStatus] = useState<undefined | 'error' | 'warning'>();

  // 手机号校验正则
  const validateEmail = (email: string) => /^1[3-9]\d{9}$/.test(email);

  useEffect(() => {
    if (value && !validateEmail(value)) {
      setInputStatus('error');
      return;
    }
    setInputStatus(undefined);
  }, [value]);

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_PHONE}_${nanoid()}`}
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
        }}
      >
        <Input
          status={InputStatus}
          readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
          defaultValue={defaultValue}
          style={{
            width: '100%',
            color,
            textAlign: align,
            backgroundColor: bgColor
          }}
          placeholder={placeholder}
          onChange={setValue}
        />
      </Form.Item>
      <div className='description showEllipsis' style={{marginLeft: labelColSpan}}>{description}</div>
    </div>
  );
});

export default XInputPhone;
