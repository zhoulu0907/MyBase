import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Card } from '@arco-design/web-react';
import { memo } from 'react';
import { type XInfoNoticeConfig } from './schema';

const XInfoNotice = memo((props: XInfoNoticeConfig) => {
  const { status, content } = props;

  return (
    <Card
      style={{
        width: '100%',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <h1>{content}</h1>
    </Card>
  );
});

export default XInfoNotice;
