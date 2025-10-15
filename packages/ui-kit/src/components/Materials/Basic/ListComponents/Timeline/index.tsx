import { Timeline } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XTimelineConfig } from './schema';

const TimelineItem = Timeline.Item;

const XTimeline = memo((props: XTimelineConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true } = props;

  return (
    <Timeline
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
      }}
    >
      <TimelineItem label="2017-03-10">The first milestone</TimelineItem>
      <TimelineItem label="2018-05-12">The second milestone</TimelineItem>
      <TimelineItem label="2020-09-30">The third milestone</TimelineItem>
    </Timeline>
  );
});

export default XTimeline;
