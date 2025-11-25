import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Radio } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XRadioConfig = typeof FormSchema.XRadioSchema.config;
const RadioGroup = Radio.Group;

const XRadio = memo((props: XRadioConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    defaultOptionsConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SWITCH}_${nanoid()}`;

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
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      layout={layout}
      rules={rules}
      initialValue={defaultOptionsConfig?.defaultOptions.find(ele => ele.isChosen)?.value}
      style={{
        margin: 0,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
        <div>--</div>
      ) : (
        <RadioGroup
          options={defaultOptionsConfig?.defaultOptions}
        />
      )}
    </Form.Item>
  );
});

export default XRadio;
