import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import { type XInputTextConfig } from './schema';

const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
    maxLength,
    runtime = true,
    detailMode
  } = props;

  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  const { form } = Form.useFormContext();
  const fieldValue = Form.useWatch(fieldId, form);

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`}
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
            defaultValue={defaultValue}
            placeholder={placeholder}
            maxLength={maxLength}
            style={{
              width: '100%',
              color,
              textAlign: align,
              backgroundColor: bgColor,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputText;
