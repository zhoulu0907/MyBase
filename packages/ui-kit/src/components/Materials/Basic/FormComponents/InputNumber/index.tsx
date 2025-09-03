import { Form, InputNumber } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputNumberConfig } from './schema';
import { nanoid } from 'platejs';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';

const XInputNumber = memo((props: XInputNumberConfig) => {
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValue,
    required,
    align,
    min,
    max,
    step,
    precision,
    layout,
    labelColSpan = 0,
    unit
  } = props;

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[
        {
          required,
          type: 'number',
          min,
          max
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
        min={min}
        max={max}
        precision={precision}
        style={{
          width: '100%',
          textAlignLast: align
        }}
        suffix={unit}
      />
    </Form.Item>
  );
});

export default XInputNumber;
