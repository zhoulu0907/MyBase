import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Ellipsis, Form, Popover, Radio, Tag } from '@arco-design/mobile-react';
import { IconQuestionCircle } from '@arco-design/mobile-react/esm/icon';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema, getFieldOptionsConfig, useAppEntityStore, menuDictSignal } from '@onebase/ui-kit';
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
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  const { appDict } = menuDictSignal;
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
      getOptions();
    }
  }, [dataField]);

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities, appDict.value);
    setOptions(newOptions);
  };

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={
        <>
          {label.display && <Ellipsis text={label.text} maxLine={2} />}
          {props?.tooltip && (
            <Popover content={props?.tooltip} direction='bottomCenter' >
              <IconQuestionCircle width={12} height={12} style={{ marginLeft: 6 }} />
            </Popover>
          )}
        </>
      }
      field={fieldId}
      layout={layout}
      rules={rules}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
        <div>{form?.getFieldValue(fieldId)?.[0] || options.find((op) => op.value === form?.getFieldValue(fieldId)?.[0])?.label || '--'}</div>
      ) : (
        <RadioGroup className='radioWrapperOBMobile'>
          {options.map((ele, index: number) => (
            <Radio key={index} value={ele.value}>
              {ele.colorType ? <Tag color='#fff' bgColor={ele.colorType} borderColor={ele.colorType}>{ele.label}</Tag> : <span>{ele.label}</span>}
            </Radio>
          ))}
        </RadioGroup>
      )}
    </Form.Item>
  );
});

export default XRadio;
