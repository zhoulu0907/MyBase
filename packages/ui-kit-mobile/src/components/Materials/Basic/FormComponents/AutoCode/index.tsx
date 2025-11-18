import { Input } from '@arco-design/mobile-react';
import { memo, useEffect, useState } from 'react';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import { type XautoCodeConfig } from './schema';

const XautoCode = memo((props: XautoCodeConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
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
    runtime = true,
    detailMode
  } = props;

  // const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  // const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="inputAutoWrapper">
      <Input
        readOnly={true}
        label={label.display && label.text}
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

      {/* <Form.Item
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue || '--'}</div>
        ) : (
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
        )}
      </Form.Item> */}
    </div>
  );
});

export default XautoCode;
