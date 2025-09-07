import { memo } from 'react';
import { Form, Carousel } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XCarouselConfig } from './schema';
import '../index.css';
import './index.css';

const XCarousel = memo((props: XCarouselConfig & { runtime?: boolean }) => {
  const { label, status, verify, layout, tooltip, labelColSpan, autoplay, interval = 3, fillStyle, carouselConfig = [], description, runtime } = props;

  return (
    <Form.Item
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }]}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
      }}
    >
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
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XCarousel;
