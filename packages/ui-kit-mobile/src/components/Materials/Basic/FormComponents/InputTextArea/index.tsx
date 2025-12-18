import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Textarea, Form, Ellipsis } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema } from '@onebase/ui-kit';
type XInputTextAreaConfig = typeof FormSchema.XInputTextAreaSchema.config;
import './index.css';
import '../index.css';

const XInputTextArea = memo((props: XInputTextAreaConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    placeholder,
    defaultValueConfig,
    verify,
    align,
    status,
    minRows = 1,
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
        maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
        showStatistics={verify?.lengthLimit}
        statisticsMaxlength={verify?.lengthLimit ? verify?.maxLength : undefined}
        autosize={false}
        rows={minRows || 2}
        textareaStyle={{
          height: 0.25 * (minRows || 2) * 2 + 'rem',
          textAlign: align
        }}
        style={{
          width: '100%'
        }}
      />
    );
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }

        if (value && verify?.lengthLimit) {
          if (value.length < verify?.minLength!) {
            callback(`字数不能小于${verify?.minLength}`);
          } else if (value.length > verify?.maxLength!) {
            callback(`字数不能大于${verify?.maxLength}`);
          }
        } else {
          callback();
        }
      }
    }
  ];

  return (
    <Form.Item
      className={`inputTextWrapperOBMobile inputTextAreaWrapperOBMobile ${verify?.lengthLimit ? 'showStatistics' : ''}`}
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
        <div className="readonlyText" style={{
          textAlign: align,
          paddingTop: '0.16rem',
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-word',
          minHeight: `${minRows * 24 + 16}px`
        }}>
          {form?.getFieldValue(fieldId)}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XInputTextArea;
