import { memo } from 'react';
import { Carousel } from '@arco-design/mobile-react';
import { STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.css';
import '../index.css';

type XCarouselConfig = typeof FormSchema.XCarouselFormSchema.config;

const XCarousel = memo((props: XCarouselConfig & { runtime?: boolean; detailMode?: boolean; }) => {
  const {
    label,
    status,
    verify,
    layout,
    tooltip,
    autoplay,
    interval = 3,
    fillStyle,
    carouselConfig = [],
    runtime,
    detailMode
  } = props;

  return (
    <div className={`inputTextWrapperOBMobile ${styles.carouselWrapperOBMobile}`}>
      <div className={styles.label}>
        {label.display && label.text ? label.text : ''}
      </div>
      <Carousel
        className={styles.carousel}
        autoPlay={autoplay}
        style={{
          pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {carouselConfig.map((img, index) => {
          const urlImg = getFileUrlById(img.fileId);
          return (
            <div className={styles.imageWrapper} key={index} onClick={() => window.open(urlImg)}>
              <img className={styles.image} src={urlImg} style={{ objectFit: fillStyle }} />
              <div className={styles.text}>{img.text}</div>
            </div>
          )
        })}
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
