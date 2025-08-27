import { Checkbox, Form } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputCheckboxConfig } from './schema';

const CheckboxGroup = Checkbox.Group;

const XCheckbox = memo((props: XInputCheckboxConfig) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    // defaultValue,
    required,
    layout,
    labelColSpan = 0,
    options
    // allChecked,
    // maxChecked = 0
  } = props;

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      // trigger="onChange"
      // triggerPropName="checked"
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required }]}
      style={{
        margin: 0,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
      // initialValue={allChecked ? options.map((op) => op.value) : []}
    >
      <CheckboxGroup
        options={options}
        style={{
          width: '100%',
          pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
        }}
      />
    </Form.Item>
  );
});

export default XCheckbox;
