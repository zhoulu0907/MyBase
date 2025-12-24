import { Carousel, Form } from '@arco-design/web-react';
import { memo } from 'react';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.css';
import { type XCarouselConfig } from './schema';

const XCarousel = memo((props: XCarouselConfig & { runtime?: boolean }) => {
  const {
    id,
    label,
    verify,
    // status,
    interval = 4,
    carouselConfig = [],
    runtime
  } = props;

  return (
    <div className={styles.carouselWrapper}>
      {label.display &&
      label.text && <div className={styles.title}>{label.text}</div>}

      <Carousel
        className={styles.carousel}
        autoPlay={runtime ? { 
          interval: interval * 1000
        } : false}
        style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        indicatorPosition="bottom"
      >
        {carouselConfig.map((img, index) => (
          <div className={styles.imageWrapper} key={index} onClick={() => window.open(img.url)}>
            <img className={styles.image} src={img.image?.indexOf('data:') < 0 ? getFileUrlById(img.image) : img.image} />
            {/* <div className={styles.text}>{img.text}</div> */}
          </div>
        ))}
      </Carousel>
    </div>
  );
});

export default XCarousel;
