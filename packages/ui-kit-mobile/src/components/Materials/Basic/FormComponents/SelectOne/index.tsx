import { Ellipsis, Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FormInternalComponentType } from '@arco-design/mobile-react/esm/form';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema, getFieldOptionsConfig, useAppEntityStore } from '@onebase/ui-kit';
import { DictData } from '@onebase/platform-center';
import '../index.css';

type XSelectOneConfig = typeof FormSchema.XSelectOneSchema.config;

const XSelectOne = memo((props: XSelectOneConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    status,
    verify,
    layout,
    runtime = true,
    defaultValueConfig,
    detailMode
  } = props;

  const { mainEntity, subEntities } = useAppEntityStore();

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`;

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
      className="inputTextWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      rules={rules}
      layout={layout}
      displayType={FormInternalComponentType.Picker}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{form?.getFieldValue(fieldId) || options.find((op) => op.id === form?.getFieldValue(fieldId)?.[0]) || '--'}</div>
      ) : (
        <Picker
          title={label?.text || ''}
          cascade={false}
          renderLinkedContainer={(a, b = []) => {
            let result: any = ''
            if (b[0]) {
              result = b[0].label
            }
            result = options.find(op => op.id === a[0])?.label || a[0]
            if (!result) {
              return <div className="arco-form-picker-link-container"><div className="arco-form-picker-link-container-placeholder">请选择</div></div>
            }
            return <div className="arco-form-picker-link-container">{result}</div>
          }}
          data={[options.map(op => ({
            label: op.label,
            value: op.id
          }))]}
          maskClosable
        />
      )}
    </Form.Item>
  );
});

export default XSelectOne;
