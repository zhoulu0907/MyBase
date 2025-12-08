import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Form } from '@arco-design/mobile-react';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css';

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />
}
type XSelectMutipleConfig = typeof FormSchema.XSelectMutipleSchema.config;

const XSelectMutiple = memo((props: XSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    verify,
    layout,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  const options = defaultOptionsConfig?.defaultOptions?.map(({ label, value }: { label: string; value: string | number }) => ({ label, value }));

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (value.length === 0 && verify?.required) {
          callback(`${label.text}是必填项`);
        }
        if (value.length > verify?.maxChecked) {
          callback(`选中数量不能大于${verify?.maxChecked}`);
        } else {
          callback();
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile selectMultipleWrapper"
      label={label.display && label.text}
      field={fieldId}
      rules={rules}
      initialValue={defaultOptionsConfig?.defaultOptions.filter(ele => ele.isChosen)?.map(ele => ele.value)}
      style={{
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div>--</div>
      ) : (
        <Checkbox.Group
          className="selectCheckoutOBMobile"
          layout='block'
          icons={squareIcon}
          options={options}
        >
        </Checkbox.Group>
      )}
    </Form.Item>
  );
});

export default XSelectMutiple;
