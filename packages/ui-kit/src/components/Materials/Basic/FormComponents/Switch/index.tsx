import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Switch } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XInputSwitchConfig } from './schema';
import '../index.css';

const XSwitch = memo((props: XInputSwitchConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, defaultValue, layout, labelColSpan = 0, description, runtime = true } = props;

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
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
      }}
    >
      <Switch defaultChecked={defaultValue === 'true'} style={{marginTop: !!description ? 4 : 0}} />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XSwitch;
