import { Input } from '@arco-design/mobile-react';
// import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
// import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputEmailConfig } from './schema';

const XInputEmail = memo((props: XInputEmailConfig & { runtime?: boolean; detailMode?: boolean }) => {
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

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="inputTextWrapper">
      <Input
        label={label.display && label.text}
        type="email"
        defaultValue={defaultValue}
        style={{
          width: '100%',
          textAlign: align,
          color: color,
          backgroundColor: bgColor,
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        placeholder={placeholder}
        inputStyle={{ textAlign: align }}
      />
      {/* <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`
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

export default XInputEmail;
