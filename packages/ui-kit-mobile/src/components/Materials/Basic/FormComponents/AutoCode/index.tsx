import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Ellipsis, Form, Input } from '@arco-design/mobile-react';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
type XautoCodeConfig = typeof FormSchema.XAutoCodeSchema.config;
import '../index.css';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    form,
    label,
    dataField,
    placeholder,
    status,
    layout,
    runtime = true,
    detailMode
  } = props;


  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.AUTO_CODE}_${nanoid()}`;

  return (
    <Form.Item
      className="inputTextWrapperOBMobile inputAutoWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} />}
      field={fieldId}
      layout={layout}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ?
        <Input
          className="readonlyText"
          readOnly
          value={form?.getFieldValue(fieldId)}
          inputStyle={{ textAlign: layout === 'vertical' ? 'left' : 'right' }}
        /> :
        <Input
          readOnly={true}
          placeholder={placeholder}
        />
      }
    </Form.Item>
  );
});

export default XautoCode;
