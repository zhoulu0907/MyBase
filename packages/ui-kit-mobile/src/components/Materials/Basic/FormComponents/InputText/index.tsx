import { Input } from '@arco-design/mobile-react';
import { memo, useEffect, useState } from 'react';
import '../index.css';
import './index.css';
import { type XInputTextConfig } from './schema';

const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    maxLength,
    runtime = true,
    detailMode
  } = props;

  console.warn('align======11====', align);
  //   const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  //   const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper inputTextWrapper">
      <Input
        label={label.display && label.text}
        defaultValue={defaultValue}
        placeholder={placeholder}
        maxLength={maxLength}
        inputStyle={{ textAlign: align }}
        style={{
          width: '100%',
          backgroundColor: bgColor,
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      />
      {/* <Form.Item
        label={label.display && label.text}
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`}
        layout={layout}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>--</div>
        ) : (
          //   <div style={{ marginLeft: '10px' }}>{fieldValue || '--'}</div>
          <Input
            defaultValue={defaultValue}
            placeholder={placeholder}
            maxLength={maxLength}
            style={{
              width: '100%',
              color,
              textAlign: align,
              backgroundColor: bgColor,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item> */}
    </div>
  );
});

export default XInputText;
