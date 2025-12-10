import { Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FormInternalComponentType } from '@arco-design/mobile-react/esm/form';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XSelectOneConfig = typeof FormSchema.XSelectOneSchema.config;

const XSelectOne = memo((props: XSelectOneConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
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
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && label.text}
      field={fieldId}
      rules={rules}
      displayType={FormInternalComponentType.Picker}
      initialValue={defaultOptionsConfig?.defaultOptions.find((ele) => ele.isChosen)?.value}
      style={{
        textAlign: align,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div>--</div>
      ) : (
        <Picker
          cascade={false}
          data={[defaultOptionsConfig.defaultOptions.map(op => op.label)]}
          maskClosable
        />
      )}
    </Form.Item>
  );
});

export default XSelectOne;
