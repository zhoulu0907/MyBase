import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Ellipsis, Form, Tag } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
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

const XCheckbox = memo((props: XCheckboxConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    label,
    align,
    dataField,
    status,
    defaultOptionsConfig,
    verify,
    layout,
    direction,
    runtime = true,
    detailMode
  } = props;

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHECKBOX}_${nanoid()}`

  // 根据是否为只读模式确定内容
  const renderContent = () => {
    // 非只读模式，渲染Input组件
    return (
      <CheckboxGroup
        className={styles.checkboxGroupOBMobile}
        layout='block'
        icons={squareIcon}
        defaultValue={defaultOptionsConfig?.defaultOptions?.filter((op) => op.chosen).map((op) => op.value)}
        options={defaultOptionsConfig?.defaultOptions}
      />
    );
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!!value.length && verify?.required) {
          callback(`${label.text}是必填项`);
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      field={fieldId}
      label={label.display && <Ellipsis text={label.text} />}
      rules={rules}
      initialValue={defaultOptionsConfig?.defaultOptions.filter(ele => ele.isChosen)?.map(ele => ele.value) || []}
      style={{
        textAlign: align || 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        // 只读模式，渲染文本内容
        <div>
          {defaultOptionsConfig?.defaultOptions.map((ele: any, index: number) => ele.isChosen && <Tag key={index}>
            {ele.label}
          </Tag>)}
        </div>
      ) : (
        renderContent()
      )}
    </Form.Item>
  );
});

export default XCheckbox;
