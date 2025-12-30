import { memo } from 'react';
import { Carousel, Ellipsis } from '@arco-design/mobile-react';
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
        {label.display && label.text ? <Ellipsis text={label.text} maxLine={2} /> : ''}
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
    </div>
  );
});

export default XCarousel;
