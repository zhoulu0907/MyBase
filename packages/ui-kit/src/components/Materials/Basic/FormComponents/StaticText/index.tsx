import { memo } from 'react';
import { Form, Input } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XStaticTextConfig } from './schema';
import './index.css';

const XStaticText = memo((props: XStaticTextConfig) => {
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
    description
  } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
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
