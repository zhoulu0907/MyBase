import { memo } from 'react';
import { Form, Input } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XautoCodeConfig } from './schema';
import '../index.css';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean }) => {
  const {
    label,
    tooltip,
    placeholder,
    status,
    defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    runtime = true
  } = props;

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        layout={layout}
        rules={[{ required: verify?.required }]}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? <div>{defaultValue || '--'}</div> :
            <Input
              readOnly={true}
              defaultValue={defaultValue}
              placeholder={placeholder}
              style={{
                width: '100%',
                color,
                textAlign: align,
                backgroundColor: bgColor,
                pointerEvents: runtime ? 'unset' : 'none'
              }}
            />
        }
      </Form.Item>
    </div>
  );
});

export default XautoCode;
