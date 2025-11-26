import { memo } from 'react';
import { ShowSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css'

const XDivider = memo((props: ShowSchema.XDividerConfig) => {
  const { content, align, margin } = props;

  return (
    <div
      className={`formWrapper dividerWrapper ${align}`}
      style={{
        margin: `${margin}px 0`,
      }}
    >
      { content && <span>{content}</span> }
    </div>
  );
});

export default XDivider;
