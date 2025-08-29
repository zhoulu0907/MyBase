import { memo } from 'react';
import { Carousel } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XCarouselConfig } from './schema';
import './index.css';

const XCarousel = memo((props: XCarouselConfig) => {
  const { status, autoplay, interval = 3, fillStyle, carouselConfig = [] } = props;

  // const imageSrc = [
  //   'https://devops.cm-iov.com:9000/system-static/img/annual2.jpg',
  //   'https://devops.cm-iov.com/static/img/bg.dd06daaa.png',
  // ];

  console.log('Carousel props', props)

  return (
    <Carousel
      className='carousel'
      autoPlay={autoplay && {
        interval: interval * 1000
      }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      {carouselConfig.map((img, index) => (
        <div className='imageWrapper' key={index} onClick={() => window.open(img.url)}>
          <img className='image' src={img.image} style={{ objectFit: fillStyle }} />
          <div className='text'>{img.text}</div>
        </div>
      ))}
    </Carousel>
  );
});

export default XCarousel;
