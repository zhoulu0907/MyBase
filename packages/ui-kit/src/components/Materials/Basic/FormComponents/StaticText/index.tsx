import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Form, Input } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { type XStaticTextConfig } from './schema';
import '../index.css';

const XStaticText = memo((props: XStaticTextConfig & { runtime?: boolean }) => {
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
    description,
    runtime = true
  } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.STATIC_TEXT}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
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
          backgroundColor: bgColor
        }}
      />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XStaticText;
