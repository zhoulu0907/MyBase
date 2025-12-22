import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Input, Form, Ellipsis } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
type XInputEmailConfig = typeof FormSchema.XInputEmailSchema.config;
import '../index.css';

const XInputEmail = memo((props: XInputEmailConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    placeholder,
    status,
    defaultValueConfig,
    verify,
    align,
    layout,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`;

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }

        if (value && !(/^[^\s@]+@[^\s@]+\.[^\s@]+$/).test(value)) {
          callback(`请输入合法的邮箱地址`);
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      rules={rules}
      label={label.display ? <Ellipsis text={label.text} /> : undefined}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div className="readonlyText">{form?.getFieldValue(fieldId)}</div>
      ) : (
        // 编辑模式，渲染Input组件
        <Input
          type="email"
          placeholder={placeholder}
          inputStyle={{ textAlign: align }}
          blockChangeWhenCompositing={true}
        />
      )}
    </Form.Item>
  );
});

export default XInputEmail;
