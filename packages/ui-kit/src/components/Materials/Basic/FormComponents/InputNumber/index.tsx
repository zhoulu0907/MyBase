import { memo } from 'react';
import { Form, InputNumber } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputNumberConfig } from './schema';
import { nanoid } from 'nanoid';
import './index.css';


const XInputNumber = memo((props: XInputNumberConfig) => {
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
    description,
    unit
  } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `XInputNumber_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[
        {
          required: verify.required,
          type: 'number',
          min: verify.min,
          max: verify.max
        }
      ]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <InputNumber
        readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
        defaultValue={defaultValue}
        placeholder={placeholder}
        step={step}
        min={verify.min}
        max={verify.max}
        precision={precision}
        style={{
          width: '100%',
          textAlignLast: align
        }}
        suffix={unit}
      />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XInputNumber;
