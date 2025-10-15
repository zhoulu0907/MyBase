import { Form, Select } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputSelectMutipleConfig } from './schema';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    labelColSpan = 0,
    showSearch,
    defaultValue,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const [fieldId, setFieldId] = useState('');

  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    if (dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1]);
    }
  }, [dataField]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }, { maxLength: verify?.maxChecked }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{/* {defaultValue.find((item: any) => item.value == fieldValue)?.label || '--'} */}</div>
        ) : (
          <Select
            mode="multiple"
            allowClear
            showSearch={showSearch}
            filterOption={(input, option) => {
              return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            placeholder="请选择"
            options={defaultValue}
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

export default XSelectMutiple;
