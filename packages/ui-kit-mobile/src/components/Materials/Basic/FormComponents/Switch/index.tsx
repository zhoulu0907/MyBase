import { Cell, Switch } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="inputTextWrapper">
      <Cell
        label={label.display && label.text}
      >
        <Switch
          platform="android"
          defaultChecked={defaultValue}
          style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Cell>

      {/* <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        triggerPropName="checked"
        wrapperCol={{ style: { flex: 1 } }}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue ? '开启' : '关闭'}</div>
        ) : (
          <Switch
            defaultChecked={defaultValue}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XSwitch;
