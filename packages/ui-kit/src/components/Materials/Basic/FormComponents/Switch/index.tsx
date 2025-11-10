import { Form, Switch } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    layout,
    fillText,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        triggerPropName="checked"
        wrapperCol={{ style: { flex: 1 } }}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {fieldValue
              ? (fillText?.display && fillText.checkedText) || '开启'
              : (fillText?.display && fillText.uncheckedText) || '关闭'}
          </div>
        ) : (
          <Switch
            checkedText={fillText?.display && fillText.checkedText}
            uncheckedText={fillText?.display && fillText.uncheckedText}
            style={{
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XSwitch;
