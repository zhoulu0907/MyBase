import { Form, Select } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputSelectOneConfig } from './schema';
import '../index.css';

const XSelectOne = memo((props: XInputSelectOneConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, showSearch, defaultValue, runtime = true } = props;

  return (
    <div className='formWrapper'>
      <Form.Item
        label={label.display && label.text}
        field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.SELECT_ONE}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={{
          style: { width: labelColSpan, flex: 'unset' }
        }}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <Select
          placeholder="请选择"
          showSearch={showSearch}
          allowClear
          options={defaultValue}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XSelectOne;
