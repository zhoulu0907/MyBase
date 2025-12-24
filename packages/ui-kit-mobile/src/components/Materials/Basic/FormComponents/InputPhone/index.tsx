import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Input, Form, Ellipsis } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, PHONE_TYPE, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
type XInputPhoneConfig = typeof FormSchema.XInputPhoneSchema.config;

import '../index.css';

const XInputPhone = memo((props: XInputPhoneConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    placeholder,
    defaultValueConfig,
    verify,
    align,
    layout,
    status,
    phoneType,
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
        type="tel"
        placeholder={placeholder}
        inputStyle={{ textAlign: layout === 'vertical' ? 'left' : 'right' }}
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
        if (phoneType === PHONE_TYPE.MOBILE) {
          if (value && !(/^1[3-9]\d{9}$/).test(value)) {
            callback(`请输入有效的11位中国大陆手机号`);
          }
        }

        if (phoneType === PHONE_TYPE.LANDLINE) {
          // (010)12345678  010-12345678
          if (value && !(/^\(?0[0-9]{2,3}\)?-?[0-9]{7,8}$/).test(value)) {
            callback(`请输入有效的座机号`);
          }
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
      label={label.display ? <Ellipsis text={label.text} /> : undefined}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <Input
          className="readonlyText"
          readOnly
          value={form?.getFieldValue(fieldId) || (defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : '')}
          inputStyle={{ textAlign: layout === 'vertical' ? 'left' : 'right' }}
        />
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputPhone;
