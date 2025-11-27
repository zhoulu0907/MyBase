import { memo } from 'react';
import { NoticeBar } from '@arco-design/mobile-react';
import IconNotice from '@arco-design/mobile-react/esm/icon/IconNotice';
import { STATUS_OPTIONS, STATUS_VALUES, ShowSchema } from '@onebase/ui-kit';

const XInfoNotice = memo((props: ShowSchema.XInfoNoticeConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, content, runtime = true } = props;

  return (
    <NoticeBar
      leftContent={<IconNotice />}
      style={{
        width: '100%',
        borderRadius: '0.16rem',
        padding: '0.12rem 0.32rem',
        boxSizing: 'border-box',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >{content}</NoticeBar>
  );
});

export default XInfoNotice;
