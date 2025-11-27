import { memo } from 'react';
import { Carousel } from '@arco-design/mobile-react';
import { STATUS_OPTIONS, STATUS_VALUES, ListSchema } from '@onebase/ui-kit';
import ImageUrl from '@/assets/images/dept_icon.svg';

const XCarousel = memo((props: ListSchema.XCarouselConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true } = props;

  return (
    <Carousel
      indicatorType="circle"
      wrapStyle={{
        padding: '0 0.24rem',
        boxSizing: 'border-box'
      }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}>
      <img src={ImageUrl} alt="" />
      <img src={ImageUrl} alt="" />
    </Carousel>
  );
});

export default XCarousel;
