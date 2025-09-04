import { Form, Select } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputSelectOneConfig } from './schema';

const XSelectOne = memo((props: XInputSelectOneConfig) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, showSearch, defaultValue } = props;

  return (
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
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Select
        placeholder="请选择"
        showSearch={showSearch}
        style={{ width: '100%' }}
        allowClear
        options={defaultValue}
      ></Select>
    </Form.Item>
  );
});

export default XSelectOne;
