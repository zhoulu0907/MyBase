import { Input, Form } from '@arco-design/mobile-react';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { nanoid } from 'nanoid';
import '../index.css';
import type { XInputNumberConfig } from './schema';

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    step,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode,
    numberFormat
  } = props;

  const { showUnit, unitValue, showPrecision, precision, showPercent, useThousandsSeparator } = numberFormat;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0 
    ? dataField[dataField.length - 1] 
    : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`;

  const detailValue = (value: number) => {
    let result = '';
    if (showPercent) {
      value = value * 100;
    }
    if (showPrecision) {
      result = value.toFixed(precision);
    }
    if (useThousandsSeparator) {
      result = `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }
    if (showPercent) {
      result = `${result}%`;
    }
    if (showUnit) {
      result = `${result}${unitValue}`;
    }

    return result.toString();
  };

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <Input
        type="number"
        placeholder={placeholder}
        maxLength={verify?.max || 1000000000}
        suffix={showUnit ? unitValue : ''}
        inputStyle={{ textAlign: align }}
        style={{
          width: '100%',
          textAlignLast: align
        }}
      />
    );
  };

  return (
    <Form.Item
      field={fieldId}
      label={label.display ? label.text : undefined}
      initialValue={defaultValue || ''}
      className="inputTextWrapper"
      rules={verify ? [{ required: verify.required, message: verify.message }] : undefined}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset'
      }}
    >
      {!runtime || detailMode ? (
        // 只读模式，渲染格式化的文本内容
        <div style={{ 
          textAlign: align, 
          padding: '8px'
        }}>
          {defaultValue !== undefined && defaultValue !== null ? detailValue(defaultValue) : '--'}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputNumber;
