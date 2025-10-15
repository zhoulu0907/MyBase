import { Form, InputNumber } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputNumberConfig } from './schema';

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    step,
    precision,
    layout,
    labelColSpan = 0,
    unit,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          {
            required: verify?.required,
            type: 'number',
            min: verify?.min,
            max: verify?.max
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
          <InputNumber
            defaultValue={defaultValue}
            placeholder={placeholder}
            step={step}
            min={verify?.min}
            max={verify?.max}
            precision={precision}
            style={{
              width: '100%',
              textAlignLast: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            suffix={unit}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputNumber;
