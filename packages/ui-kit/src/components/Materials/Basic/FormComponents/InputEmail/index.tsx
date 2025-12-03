// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES } from '../../../constants';
import type { XInputEmailConfig } from './schema';

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XInputEmail = memo((props: XInputEmailConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[
          { required: verify?.required, message:`${label.text}是必填项` },
          {
            match: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            message: '请输入合法的邮箱地址'
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          flex: 1,
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
          <Input
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

export default XInputEmail;
