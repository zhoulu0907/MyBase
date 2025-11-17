import { Carousel } from '@arco-design/mobile-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import styles from './index.module.css';
import { type XCarouselConfig } from './schema';

const XCarousel = memo((props: XCarouselConfig & { runtime?: boolean }) => {
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
    runtime
  } = props;

  return (
    <div className="inputTextWrapper">
      <div className={styles.label}>
        {label.display && label.text ? label.text : ''}
      </div>
      <Carousel
        className={styles.carousel}
        autoPlay={autoplay}
        style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}
      >
        {carouselConfig.map((img, index) => (
          <div className={styles.imageWrapper} key={index} onClick={() => window.open(img.url)}>
            <img className={styles.image} src={img.image} style={{ objectFit: fillStyle }} />
            <div className={styles.text}>{img.text}</div>
          </div>
        ))}
      </Carousel>
      <div className={styles.bottomDivider}></div>

      {/* <Form.Item
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <Carousel
          className={styles.carousel}
          autoPlay={
            autoplay && {
              interval: interval * 1000
            }
          }
          style={{
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        >
          {carouselConfig.map((img, index) => (
            <div className={styles.imageWrapper} key={index} onClick={() => window.open(img.url)}>
              <img className={styles.image} src={img.image} style={{ objectFit: fillStyle }} />
              <div className={styles.text}>{img.text}</div>
            </div>
          ))}
        </Carousel>
      </Form.Item> */}
    </div>
  );
});

export default XCarousel;
