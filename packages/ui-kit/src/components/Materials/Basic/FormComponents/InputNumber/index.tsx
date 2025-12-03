// ===== 导入 begin =====
import { Form, InputNumber } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputNumberConfig } from './schema';

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
  const {
    label,
    placeholder,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    align,
    step,
    layout,
    runtime = true,
    detailMode,
    numberFormat
  } = props;
    const { showUnit, unitValue, showPrecision, precision, showPercent, useThousandsSeparator } = numberFormat;
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

  // ===== 内部事件 =====
  // ===== 内部事件 =====

  // ===== 方法：帮助方法 begin =====
  const helpers = {
    detailValue: (value: number) => {
      let result = (value || '').toString();
      if (!value) {
        return result;
      } else {
        value = Number(value);
      }
      if (showPercent) {
        value = value * 100;
      }
      if (showPrecision && value) {
        result = Number(value).toFixed(precision);
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

      return (result || '').toString();
    }
  }
  // ===== 方法：帮助方法 end =====

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { style: { width: 200, flex: 'unset' } } : {}}
        rules={[
          {
            required: verify?.required,
            message: `${label.text}是必填项`
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
          <div>{helpers.detailValue(fieldValue) || '--'}</div>
        ) : (
          <InputNumber
            placeholder={placeholder}
            step={step}
            min={verify?.numberLimit ? verify?.min : undefined}
            max={verify?.numberLimit ? verify?.max : undefined}
            precision={showPrecision ? precision : 0}
            formatter={(value) => {
              return useThousandsSeparator ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : value.toString();
            }}
            parser={(value) => value.replace(/,/g, '')}
            style={{
              width: '100%',
              textAlignLast: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            suffix={showUnit ? unitValue : ''}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputNumber;
