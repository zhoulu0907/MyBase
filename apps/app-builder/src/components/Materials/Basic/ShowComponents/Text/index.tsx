import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { memo } from 'react';
import { type XTextConfig } from './schema';

const XText = memo((props: XTextConfig) => {
  const { status, content } = props;

  return (
    <div
      style={{
        width: '100%',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      {content}
    </div>
  );
});

export default XText;
