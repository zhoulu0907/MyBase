import { memo, useEffect, useState } from 'react';
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputPhoneConfig } from './schema';
import '../index.css';

const XInputPhone = memo((props: XInputPhoneConfig) => {
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
    description
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
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `XInputPhone_${nanoid()}`}
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
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XInputPhone;
