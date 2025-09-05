import { Carousel, Form } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import { type XCarouselConfig } from './schema';

const XCarousel = memo((props: XCarouselConfig) => {
  const {
    label,
    status,
    verify,
    layout,
    tooltip,
    labelColSpan,
    autoplay,
    interval = 3,
    fillStyle,
    carouselConfig = [],
    description
  } = props;

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
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Carousel
        className="carousel"
        autoPlay={
          autoplay && {
            interval: interval * 1000
          }
        }
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
        }}
      >
        {carouselConfig.map((img, index) => (
          <div className="imageWrapper" key={index} onClick={() => window.open(img.url)}>
            <img className="image" src={img.image} style={{ objectFit: fillStyle }} />
            <div className="text">{img.text}</div>
          </div>
        ))}
      </Carousel>
    </Form.Item>
  );
});

export default XCarousel;
