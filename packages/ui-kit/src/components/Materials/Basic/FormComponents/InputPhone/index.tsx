// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, PHONE_TYPE } from '../../../constants';
import type { XInputPhoneConfig } from './schema';
import { securityEncodeText } from '@/utils';

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
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
    detailMode,
    security
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  const [fieldId, setFieldId] = useState('');

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);
  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const {
    form,
    fieldValue
  } = useFormFieldWatch(fieldId);
  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

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
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          { required: verify?.required, message: `${label.text}是必填项` },
          {
            validator: (value, callback) => {
              if (phoneType === PHONE_TYPE.MOBILE) {
                if (value && !(/^1[3-9]\d{9}$/).test(value)) {
                  callback(`请输入有效的11位中国大陆手机号`);
                }
              }
              if (phoneType === PHONE_TYPE.LANDLINE) {
                // (010)12345678  010-12345678
                if (value && !(/^\(?0[0-9]{2,3}\)?-?[0-9]{7,8}$/).test(value)) {
                  callback(`请输入有效的座机号`);
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
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{securityEncodeText(security, fieldValue)}</div>
        ) : (
          <Input
            prefix={phoneType === PHONE_TYPE.MOBILE ? '+86' : null}
            maxLength={phoneType === PHONE_TYPE.MOBILE ? 11 : 15}
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
