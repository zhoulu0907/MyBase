import { Form, Select } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputSelectMutipleConfig } from './schema';
import '../index.css';

const XSelectMutiple = memo((props: XInputSelectMutipleConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, verify, layout, labelColSpan = 0, showSearch, defaultValue, description, runtime = true } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }, { maxLength: verify.maxChecked }]}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
      }}
    >
      <Select
        mode="multiple"
        allowClear
        showSearch={showSearch}
        placeholder="请选择"
        style={{ width: '100%' }}
        options={defaultValue}
      />
      <div className="description showEllipsis">{description}</div>
    </Form.Item>
  );
});

export default XSelectMutiple;
