import { Form, InputNumber } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputNumberConfig } from './schema';
import '../index.css';

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean }) => {
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
    runtime = true
  } = props;

  return (
    <div className='formWrapper'>
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
        {
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? <div>{defaultValue || '--'}</div> :
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
        }
      </Form.Item>
    </div>
  );
});

export default XInputNumber;
