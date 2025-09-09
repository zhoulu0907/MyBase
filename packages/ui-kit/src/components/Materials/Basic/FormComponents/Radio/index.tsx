import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Radio } from '@arco-design/web-react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputRadioConfig } from './schema';
import '../index.css';

const RadioGroup = Radio.Group;

const XRadio = memo((props: XInputRadioConfig & { runtime?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    direction,
    description,
    runtime = true
  } = props;

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`}
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
        }}
      >
        <RadioGroup direction={direction} options={defaultValue} defaultValue={defaultValue?.find(op => op.chosen)?.value} />
      </Form.Item>
      <div className='description showEllipsis' style={{marginLeft: labelColSpan}}>{description}</div>
    </div>
  );
});

export default XRadio;
