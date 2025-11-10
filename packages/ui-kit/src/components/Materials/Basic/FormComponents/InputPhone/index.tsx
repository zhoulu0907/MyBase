import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, PHONE_TYPE } from '../../../constants';
import '../index.css';
import type { XInputPhoneConfig } from './schema';

const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    phoneType,
    align,
    layout,
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
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_PHONE}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          { required: verify?.required },
          {
            validator: (value, callback) => {
              if (phoneType === PHONE_TYPE.MOBILE) {
                if (!(/^1[3-9]\d{9}$/).test(value)) {
                  callback(`请输入有效的11位中国大陆手机号`);
                }
              }
            }
          },
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
            prefix={phoneType === PHONE_TYPE.MOBILE ? '+86' : null}
            style={{
              width: '100%',
              textAlign: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            placeholder={placeholder}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputPhone;
