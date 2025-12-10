import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import { type XStaticTextConfig } from './schema';

const XStaticText = memo((props: XStaticTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    status,
    defaultValue,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    maxLength,
    runtime = true
  } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.STATIC_TEXT}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <Input
          readOnly={true}
          placeholder={placeholder}
          value={defaultValue}
          maxLength={maxLength}
          style={{
            width: '100%',
            color,
            textAlign: align,
            backgroundColor: bgColor,
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XStaticText;
