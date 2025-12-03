// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import type { XInputTextAreaConfig } from './schema';

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

const TextArea = Input.TextArea;

// ===== 组件定义 begin =====
const XInputTextArea = memo((props: XInputTextAreaConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
    minRows,
    runtime = true,
    detailMode
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
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXTAREA}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[
          { required: verify?.required, message:`${label.text}是必填项` },
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
          <div>{fieldValue || '--'}</div>
        ) : (
          <TextArea
            key={`${props.id}-TextArea`}
            placeholder={placeholder}
            maxLength={verify.lengthLimit ? verify.maxLength : undefined}
            allowClear
            rows={minRows}
            showWordLimit
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

export default XInputTextArea;
