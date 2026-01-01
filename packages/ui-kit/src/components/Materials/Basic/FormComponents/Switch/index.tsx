import { Form, Switch } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import { useFormFieldWatch } from '../useFormField';
import type { XInputSwitchConfig } from './schema';

const XSwitch = memo((props: XInputSwitchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    align,
    layout,
    fillText,
    runtime = true,
    detailMode
  } = props;

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`

  const { form, fieldValue } = useFormFieldWatch(dataField);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        triggerPropName="checked"
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : false}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {fieldValue
              ? (fillText?.display && fillText.checkedText) || '开启'
              : (fillText?.display && fillText.uncheckedText) || '关闭'}
          </div>
        ) : (
          <div style={{ width: '100%', textAlign: align }}>
            <Switch
              onChange={(value) => {
                form.setFieldValue(fieldId, value);
              }}
              checked={fieldValue}
              checkedText={fillText?.display && fillText.checkedText}
              uncheckedText={fillText?.display && fillText.uncheckedText}
              style={{
                pointerEvents: runtime ? 'unset' : 'none'
              }}
            />
          </div>
        )}
      </Form.Item>
    </div>
  );
});
export default XSwitch;
