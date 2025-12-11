import { memo } from 'react';
import { Divider } from '@arco-design/mobile-react';
import { ShowSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css'
type XDividerConfig = typeof ShowSchema.XDividerSchema.config;

const XDivider = memo((props: XDividerConfig) => {
  const { content, align, margin } = props;

  return (
    <div
      className={`formWrapperOBMobile dividerWrapperOBMobile ${align}`}
      style={{
        margin: `${margin / 100 * 2}rem 0`,
      }}
    >
      <Divider content={content} align={align} />
    </div>
  );
});

export default XDivider;
