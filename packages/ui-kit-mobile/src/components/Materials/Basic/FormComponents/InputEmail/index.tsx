import { Input, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputEmailConfig } from './schema';

const XInputEmail = memo((props: XInputEmailConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`;

  // 构建验证规则
  const rules = [];
  if (verify?.required) {
    rules.push({ required: true, message: '请输入邮箱地址' });
  }
  // 邮箱格式验证规则
  rules.push({
    match: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: '请输入合法的邮箱地址'
  });

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || ''}
      className="inputTextWrapper"
      rules={rules}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset'
      }}
    >
      {!runtime || detailMode ? (
        // 只读模式，渲染文本内容
        <div style={{
          textAlign: align,
          color: color,
          backgroundColor: bgColor || 'transparent',
          padding: '8px'
        }}>
          {defaultValue || '--'}
        </div>
      ) : (
        // 编辑模式，渲染Input组件
        <Input
          type="email"
          placeholder={placeholder}
          style={{
            width: '100%',
            textAlign: align,
            color: color,
            backgroundColor: bgColor || 'transparent'
          }}
          inputStyle={{ textAlign: align }}
        />
      )}
    </Form.Item>
  );
});

export default XInputEmail;
