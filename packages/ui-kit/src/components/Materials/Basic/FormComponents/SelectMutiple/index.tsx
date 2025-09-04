import { memo } from 'react';
import { Form, Select } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputSelectMutipleConfig } from './schema';
import './index.css';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig) => {
  const { label, tooltip, status, verify, layout, labelColSpan = 0, showSearch, defaultValue, description } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify.required }, { maxLength: verify.maxChecked }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Select
        mode="multiple"
        allowClear
        showSearch={showSearch}
        placeholder="Select"
        style={{ width: '100%' }}
        options={defaultValue}
      />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XSelectMutiple;
