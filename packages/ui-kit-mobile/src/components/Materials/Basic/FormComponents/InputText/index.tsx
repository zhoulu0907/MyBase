import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Input, Form, Ellipsis } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema, securityEncodeText } from '@onebase/ui-kit';
type XInputTextConfig = typeof FormSchema.XInputTextSchema.config;

import '../index.css';

const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    placeholder,
    defaultValueConfig,
    status,
    verify,
    align,
    layout,
    security,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;
  
  const textAlign = layout === 'vertical' ? 'left' : 'right';

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <Input
        placeholder={placeholder}
        maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
        inputStyle={{ textAlign }}
        blockChangeWhenCompositing={true}
      />
    );
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`,
      validator: (value, callback) => {
        if (value && verify?.lengthLimit) {
           if (verify?.minLength && value.length < verify.minLength) {
            callback(`字数不能小于${verify?.minLength}`);
          } else if (verify?.maxLength && value.length > verify.maxLength) {
            callback(`字数不能大于${verify?.maxLength}`);
          }
        }
        callback();
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
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div
          className="readonlyText"
          style={{ textAlign }}
        >{securityEncodeText(security, form?.getFieldValue(fieldId))}</div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputText;
