import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XTextConfig } from './schema';
import '../index.css';

const XText = memo((props: XTextConfig & { runtime?: boolean }) => {
  const { status, content, color } = props;

  return (
    <div
      className='formWrapper'
      style={{
        color,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {content}
    </div>
  );
});

export default XText;
