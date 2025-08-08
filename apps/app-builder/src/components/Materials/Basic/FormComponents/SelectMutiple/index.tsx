import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Form, Select } from '@arco-design/web-react';
import { memo } from 'react';
import type { XInputSelectMutipleConfig } from './schema';

const Option = Select.Option;
const options = ['Beijing', 'Shanghai', 'Guangzhou', 'Disabled'];

const XSelectMutiple = memo((props: XInputSelectMutipleConfig) => {
  const { label, tooltip, status, required, layout, labelColSpan = 0 } = props;

  return (
    <Form.Item
      label={label}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        span: labelColSpan
      }}
      wrapperCol={{ span: 24 - labelColSpan }}
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Select
        mode="multiple"
        placeholder="Select"
        style={{ width: '100%' }}
        defaultValue={['Beijing', 'Shenzhen']}
        allowClear
      >
        {options.map((option, index) => (
          <Option key={option} disabled={index === 3} value={option}>
            {option}
          </Option>
        ))}
      </Select>
    </Form.Item>
  );
});

export default XSelectMutiple;
