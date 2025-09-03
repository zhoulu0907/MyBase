import { Form, Select } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputSelectOneConfig } from './schema';
import './index.css';

const Option = Select.Option;
const options = ['Beijing', 'Shanghai', 'Guangzhou', 'Disabled'];

const XSelectOne = memo((props: XInputSelectOneConfig) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, showSearch, description } = props;

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
      rules={[{ required: verify.required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Select placeholder="Select" showSearch={showSearch} style={{ width: '100%' }} allowClear>
        {options.map((option, index) => (
          <Option key={option} disabled={index === 3} value={option}>
            {option}
          </Option>
        ))}
      </Select>
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XSelectOne;
