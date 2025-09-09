import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Form } from '@arco-design/web-react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputCheckboxConfig } from './schema';
import '../index.css';

const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig & { runtime?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    direction,
    labelColSpan = 0,
    // allChecked,
    runtime = true,
    description,
  } = props;

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        triggerPropName='checked'
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
        }}
      >
        <CheckboxGroup
          defaultValue={defaultValue?.filter(op => op.chosen).map(op => op.value)}
          options={defaultValue}
          direction={direction}
          style={{
            width: '100%',
            pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
          }}
        />
      </Form.Item>
      <div className='description showEllipsis' style={{marginLeft: labelColSpan}}>{description}</div>
    </div>
  );
});

export default XCheckbox;
