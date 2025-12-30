import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Ellipsis, Form } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema, useAppEntityStore, DEFAULT_VALUE_TYPES, getFieldOptionsConfig } from '@onebase/ui-kit';
import { DictData } from '@onebase/platform-center';
import styles from './index.module.css';
import '../index.css';

type XCheckboxConfig = typeof FormSchema.XCheckboxSchema.config;
const CheckboxGroup = Checkbox.Group;

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />,
}

const XCheckbox = memo((props: XCheckboxConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any; }) => {
  const {
    form,
    label,
    align,
    dataField,
    status,
    defaultValueConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  const { mainEntity, subEntities } = useAppEntityStore();
  const [options, setOptions] = useState<DictData[]>([]);

  const textAlign = layout === 'vertical' ? 'left' : 'right';
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`

  useEffect(() => {
    if (dataField?.length) {
      getOptions()
    }
  }, [dataField])

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
    setOptions(newOptions)
  }

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <CheckboxGroup
        className={styles.checkboxGroupOBMobile}
        layout={direction === 'vertical' ? 'block' : 'inline'}
        icons={squareIcon}
        options={options}
        style={{ alignItems: layout === 'vertical' ? 'flex-start' : 'flex-end' }}
      />
    );
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  const readonlyText = form?.getFieldValue(fieldId) && Array.isArray(form?.getFieldValue(fieldId)) ? form?.getFieldValue(fieldId).map((ele: any, index: number) => <div key={index} style={{ marginBottom: '0' }}>
    {ele?.name || options.find((e => e.id === ele || e.id === ele?.id))?.label}
  </div>) : '--';

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      rules={rules}
      layout={layout}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div className="readonlyText" style={{ textAlign }}>{readonlyText}</div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XCheckbox;
