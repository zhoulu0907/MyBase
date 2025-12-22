import { Ellipsis, Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FormInternalComponentType } from '@arco-design/mobile-react/esm/form';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XSelectOneConfig = typeof FormSchema.XSelectOneSchema.config;

const XSelectOne = memo((props: XSelectOneConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    status,
    verify,
    layout,
    align,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`;

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} />}
      field={fieldId}
      rules={rules}
      displayType={FormInternalComponentType.Picker}
      style={{
        textAlign: align,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{form?.getFieldValue(fieldId)}</div>
      ) : (
        <Picker
          title={label?.text || ''}
          cascade={false}
          renderLinkedContainer={(a, b = []) => {
            let result: any = ''
            if (b[0]) {
              result = b[0].label
            }
            result = defaultOptionsConfig.defaultOptions.find(op => op.value === a[0])?.label || a[0]
            if (!result) {
              return <div className="arco-form-picker-link-container"><div className="arco-form-picker-link-container-placeholder">请选择</div></div>
            }
            return <div className="arco-form-picker-link-container">{result}</div>
          }}
          data={[defaultOptionsConfig.defaultOptions.map(op => ({
            label: op.label,
            value: op.value
          }))]}
          maskClosable
        />
      )}
    </Form.Item>
  );
});

export default XSelectOne;
