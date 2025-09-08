import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XTextConfig } from './schema';

const XText = memo((props: XTextConfig & { runtime?: boolean }) => {
  const { status, content, runtime = true } = props;

  return (
    <div
      style={{
        width: '100%',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
      }}
    >
      {content}
    </div>
  );
});

export default XText;
