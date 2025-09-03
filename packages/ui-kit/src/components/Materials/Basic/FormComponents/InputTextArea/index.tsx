import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'platejs';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputTextAreaConfig } from './schema';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    required,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    minLength = 0,
    maxLength = 0,
    minRows,
    maxRows
  } = props;
  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXTAREA}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[
        { required },
        {
          validator: (value, callback) => {
            if (minLength !== 0 && value.length < minLength) {
              callback(`字数不能小于${minLength}`);
            }
            if (maxLength !== 0 && value.length > maxLength) {
              callback(`字数不能大于${maxLength}`);
            }
          }
        }
      ]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <TextArea
        readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
        defaultValue={defaultValue}
        placeholder={placeholder}
        maxLength={maxLength}
        allowClear
        autoSize={{
          minRows,
          maxRows
        }}
        showWordLimit
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

export default XInputTextArea;
