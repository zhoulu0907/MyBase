import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Input } from '@arco-design/mobile-react';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
type XautoCodeConfig = typeof FormSchema.XAutoCodeSchema.config;
import './index.css';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
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
    : `${FORM_COMPONENT_TYPES.INPUT_EMAIL}_${nanoid()}`;

  return (
    <div className="inputAutoWrapper">
      <Form.Item
        label={label.display && label.text}
        field={fieldId}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>--</div>
        ) : (
          <Input
            readOnly={true}
            placeholder={placeholder}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XautoCode;
