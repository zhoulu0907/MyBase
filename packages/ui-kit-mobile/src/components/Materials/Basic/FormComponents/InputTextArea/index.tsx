import { Textarea, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import './index.css';
import { type XInputTextAreaConfig } from './schema';

const XInputTextArea = memo((props: XInputTextAreaConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    defaultValue,
    verify,
    align,
    color,
    bgColor,
    minRows = 1,
    maxRows,
    maxLength,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXTAREA}_${nanoid()}`;

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Textarea组件
    // 完全按照InputText组件的方式实现，简化配置
    return (
      <Textarea
        border="none"
        placeholder={placeholder}
        maxLength={maxLength}
        showStatistics={maxLength !== undefined}
        statisticsMaxlength={maxLength}
        autoSize={maxRows ? { minRows, maxRows } : { minRows }}
        textareaStyle={{
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

  // 简化验证规则，与InputText保持一致
  const rules = verify ? [
    { required: verify.required, message: '此项为必填项' }
  ] : undefined;

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || ''}
      className={`inputTextWrapper inputTextAreaWrapper ${maxLength ? 'showStatistics' : ''}`}
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
          padding: '8px',
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-word',
          minHeight: `${minRows * 24 + 16}px`
        }}>
          {defaultValue || '--'}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputTextArea;
