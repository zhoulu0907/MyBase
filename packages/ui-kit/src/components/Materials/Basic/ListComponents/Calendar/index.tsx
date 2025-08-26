import { Calendar } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XCalendarConfig } from './schema';

const XCalendar = memo((props: XCalendarConfig) => {
  const { status } = props;

  return (
    <Calendar
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    />
  );
});

export default XCalendar;
