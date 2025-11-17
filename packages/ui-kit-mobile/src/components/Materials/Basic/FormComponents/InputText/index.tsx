import { Input, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import { type XInputTextConfig } from './schema';

const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
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
    maxLength,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <Input
        placeholder={placeholder}
        maxLength={maxLength}
        inputStyle={{
          textAlign: align,
          color: color
        }}
        style={{
          width: '100%',
          backgroundColor: bgColor || 'transparent',
          color: color
        }}
      />
    );
  };

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || ''}
      className="formWrapper inputTextWrapper"
      rules={verify ? [{ required: verify.required, message: verify.message }] : undefined}
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
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputText;
