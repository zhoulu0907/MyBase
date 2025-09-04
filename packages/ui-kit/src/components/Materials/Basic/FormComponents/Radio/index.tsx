import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Radio } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XInputRadioConfig } from './schema';
import './index.css';

const RadioGroup = Radio.Group;
const XRadio = memo((props: XInputRadioConfig) => {
  const { label, dataField, tooltip, status, defaultValue, verify, layout, labelColSpan = 0, direction, description } = props;

  return (
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
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <RadioGroup direction={direction} options={defaultValue} />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XRadio;
