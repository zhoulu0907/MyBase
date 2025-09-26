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
          { required: verify?.required },
          {
            match: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            message: '请输入合法的邮箱地址'
          }
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
        }
      </Form.Item>
    </div>
  );
});

export default XInputEmail;
