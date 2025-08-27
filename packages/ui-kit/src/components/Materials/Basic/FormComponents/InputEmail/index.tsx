import { Form, Input } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputEmailConfig } from './schema';

const XInputEmail = memo((props: XInputEmailConfig) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    required,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0
  } = props;

  const [value, setValue] = useState('');
  const [InputStatus, setInputStatus] = useState<undefined | 'error' | 'warning'>();

  // 邮箱校验正则
  const validateEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  useEffect(() => {
    if (value && !validateEmail(value)) {
      setInputStatus('error');
      return;
    }
    setInputStatus(undefined);
  }, [value]);

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[
        { required }
        // { type: "email", message: "请输入合法的邮件地址" },
        // {
        //     validator: (value) => {
        //         if (!value) return true;
        //         const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        //         return regex.test(value);
        //     },
        // },
      ]}
      style={{
        flex: 1,
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
    </Form.Item>
  );
});

export default XInputEmail;
