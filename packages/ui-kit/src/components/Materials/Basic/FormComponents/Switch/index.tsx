import { Form, Switch } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig) => {
  const { label, dataField, tooltip, status, defaultValue, required, layout, labelColSpan = 0 } = props;

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Switch defaultChecked={defaultValue === 'true'} />
    </Form.Item>
  );
});

export default XSwitch;
