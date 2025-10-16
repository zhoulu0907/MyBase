import { Calendar } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XCalendarConfig } from './schema';

const XCalendar = memo((props: XCalendarConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true } = props;

  return (
    <Calendar
      style={{
        margin: 0,
        padding: 6,
        borderRadius: 8,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}
    />
  );
});

export default XCalendar;
