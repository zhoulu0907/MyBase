import { memo } from 'react';
import { Divider, Form } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XDividerConfig } from './schema';

const XDivider = memo((props: XDividerConfig & { runtime?: boolean }) => {
  const { label, tooltip, status, defaultValue, align, layout, labelColSpan = 0, margin, runtime = true } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
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
