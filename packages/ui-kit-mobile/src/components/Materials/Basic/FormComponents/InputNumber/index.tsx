import { Ellipsis, Form, Input } from '@arco-design/mobile-react';
import { ITypeRules, ValidatorType } from '@arco-design/mobile-utils';
import { nanoid } from 'nanoid';
import { memo } from 'react';

import {
  DEFAULT_VALUE_TYPES,
  FORM_COMPONENT_TYPES,
  FormSchema,
  securityEncodeText,
  STATUS_OPTIONS,
  STATUS_VALUES
} from '@onebase/ui-kit';
import '../index.css';
type XInputNumberConfig = typeof FormSchema.XInputNumberSchema.config;

const XInputNumber = memo((props: XInputNumberConfig & { runtime?: boolean; detailMode?: boolean; form?: any }) => {
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
    numberFormat,
    security,
    form
  } = props;

  const { showUnit, unitValue, showPrecision, precision, showPercent, useThousandsSeparator } = numberFormat;

  // 生成唯一的字段ID
  const fieldId =
    dataField && dataField.length > 0
      ? dataField[dataField.length - 1]
      : `${FORM_COMPONENT_TYPES.INPUT_NUMBER}_${nanoid()}`;

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
  };

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <Input
        className="no-spin"
        type="number"
        placeholder={placeholder}
        maxLength={verify?.max || Infinity}
        suffix={showUnit ? unitValue : ''}
        blockChangeWhenCompositing={true}
        inputStyle={{ textAlign: layout === 'vertical' ? 'left' : 'right' }}
      />
    );
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`,
      validator: (value, callback) => {
        if (value && verify?.numberLimit) {
          if (verify?.min != null && value < verify?.min) {
            callback(`数字不能小于${verify?.min}`);
          } else if (verify?.max != null && value > verify?.max) {
            callback(`数字不能大于${verify?.max}`);
          }
        } else {
          callback();
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      rules={rules}
      layout={layout}
      label={label.display ? <Ellipsis text={label.text} maxLine={2} /> : undefined}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      style={{
        pointerEvents: !runtime || detailMode ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染格式化的文本内容
        <div className="readonlyText">
          {securityEncodeText(security, helpers.detailValue(form?.getFieldValue(fieldId)))}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputNumber;
