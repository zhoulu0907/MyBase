import { Divider, Form } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XDividerConfig } from './schema';

const XDivider = memo((props: XDividerConfig) => {
  const { label, tooltip, status, defaultValue, align, layout, labelColSpan = 0, margin } = props;

  return (
    <Form.Item
      label={label}
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
      <Divider
        orientation={align}
        type={layout}
        style={{
          margin: `${margin}px 0`
        }}
      >
        {defaultValue}
      </Divider>
    </Form.Item>
  );
});

export default XDivider;
