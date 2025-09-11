import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputEmailConfig } from './schema';
import '../index.css';

const XInputEmail = memo((props: XInputEmailConfig & { runtime?: boolean }) => {
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
    runtime = true
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
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          { required: verify?.required }
          // { type: "email", message: "请输入合法的邮件地址" },
          // {
          //     validator: (value) => {
          //         if (!value) return true;
          //         const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          //         return regex.test(value);
          //     },
          // },
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          flex: 1,
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? <div>{defaultValue || '--'}</div> :
            <Input
              status={InputStatus}
              defaultValue={defaultValue}
              style={{
                width: '100%',
                color,
                textAlign: align,
                backgroundColor: bgColor,
                pointerEvents: runtime ? 'unset' : 'none'
              }}
              placeholder={placeholder}
              onChange={setValue}
            />
        }
      </Form.Item>
    </div>
  );
});

export default XInputEmail;
