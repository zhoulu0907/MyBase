import { Card } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XInfoNoticeConfig } from './schema';

const XInfoNotice = memo((props: XInfoNoticeConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, content, runtime = true } = props;

  return (
    <Card
      style={{
        width: '100%',
        borderRadius: 8,
        boxSizing: 'border-box',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}
    >
      <h1>{content}</h1>
    </Card>
  );
});

export default XInfoNotice;
