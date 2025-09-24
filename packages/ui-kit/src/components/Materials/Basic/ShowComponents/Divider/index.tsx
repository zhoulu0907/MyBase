import { memo } from 'react';
import { Divider } from '@arco-design/web-react';
import { type XDividerConfig } from './schema';

const XDivider = memo((props: XDividerConfig) => {
  const { content, align, margin } = props;

  return (
    <Divider
      className='formWrapper'
      orientation={align}
      style={{
        margin: `${margin}px 0`
      }}
    >
      {content}
    </Divider>
  );
});

export default XDivider;
