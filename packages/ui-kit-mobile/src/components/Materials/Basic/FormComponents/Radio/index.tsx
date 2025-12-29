import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Ellipsis, Form, Radio } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema, getFieldOptionsConfig, useAppEntityStore, DEFAULT_VALUE_TYPES } from '@onebase/ui-kit';
import { DictData } from '@onebase/platform-center';
import '../index.css';
import './index.css';

type XRadioConfig = typeof FormSchema.XRadioSchema.config;
const RadioGroup = Radio.Group;

const XRadio = memo((props: XRadioConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  const { mainEntity, subEntities } = useAppEntityStore();

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`;

  const [options, setOptions] = useState<DictData[]>([]);

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  useEffect(() => {
    if (dataField?.length) {
      getOptions()
    }
  }, [dataField])

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
    setOptions(newOptions)
  }

  return (
    <Form.Item
      className="inputTextWrapperOBMobile radioWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      layout={layout}
      rules={rules}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
        <div>{form?.getFieldValue(fieldId)?.name || options.find((op) => op.id === form?.getFieldValue(fieldId)?.id || op.id === form?.getFieldValue(fieldId))?.label || '--'}</div>
      ) : (
        <RadioGroup
          options={options}
        />
      )}
    </Form.Item>
  );
});

export default XRadio;
