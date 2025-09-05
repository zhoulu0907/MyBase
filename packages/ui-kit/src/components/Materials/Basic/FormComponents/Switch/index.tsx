import { Form, Switch } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig) => {
  const { label, dataField, tooltip, status, defaultValue, layout, labelColSpan = 0, description } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Switch defaultChecked={defaultValue === 'true'} style={{ marginTop: !!description ? 4 : 0 }} />
    </Form.Item>
  );
});

export default XSwitch;
