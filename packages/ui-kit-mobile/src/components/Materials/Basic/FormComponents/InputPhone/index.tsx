import { Input, Form } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import '../index.css';
import type { XInputPhoneConfig } from './schema';

const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    defaultValue,
    verify,
    align,
    color,
    bgColor,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_PHONE}_${nanoid()}`;

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <Input
        placeholder={placeholder}
        inputStyle={{
          textAlign: align,
          color: color
        }}
        style={{
          width: '100%',
          backgroundColor: bgColor || 'transparent',
          color: color
        }}
        type="tel"
      />
    );
  };

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || ''}
      className="inputTextWrapper"
      rules={[
        ...(verify?.required ? [{ required: true, message: '请输入手机号' }] : []),
        {
          match: /^1[3-9]\d{9}$/,
          message: '请输入有效的11位中国大陆手机号'
        }
      ]}
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

export default XInputPhone;
