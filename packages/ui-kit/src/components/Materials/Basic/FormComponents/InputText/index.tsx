// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XInputTextConfig } from './schema';
import { securityEncodeText } from '@/utils'

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean; cpState?: any }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValueConfig,
    verify,
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
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          { required: verify?.required, message: `${label.text}是必填项` },
          {
            validator: (value, callback) => {
              if (verify.lengthLimit) {
                if (verify.minLength && value && value.length < verify.minLength) {
                  callback(`字数不能小于${verify.minLength}`);
                }
                if (verify.maxLength && value && value.length > verify.maxLength) {
                  callback(`字数不能大于${verify.maxLength}`);
                }
              }
            }
          }
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
            placeholder={placeholder}
            maxLength={verify.lengthLimit ? verify.maxLength : undefined}
            style={{
              width: '100%',
              textAlign: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputText;
