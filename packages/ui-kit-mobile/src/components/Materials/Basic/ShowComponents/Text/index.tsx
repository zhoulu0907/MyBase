import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, ShowSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css'
type XTextConfig = typeof ShowSchema.XTextSchema.config;

const XText = memo((props: XTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, content, color } = props;

  return (
    <div className="formWrapper">
      <div
        className="formWrapper--text"
        style={{
          color,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {content}
      </div>
    </div>
  );
});

export default XText;
