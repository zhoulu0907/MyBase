import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import { type XInputTextConfig } from './schema';

const XInputText = memo((props: XInputTextConfig & { runtime?: boolean }) => {
  const { runtime = true } = props;
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
    description
  } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`}
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
      <Input
        readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
        defaultValue={defaultValue}
        placeholder={placeholder}
        maxLength={maxLength}
        style={{
          width: '100%',
          color,
          textAlign: align,
          backgroundColor: bgColor
        }}
      />
    </Form.Item>
  );
});

export default XInputText;
