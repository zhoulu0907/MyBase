import { Carousel } from '@arco-design/mobile-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, workbenchSchema } from '@onebase/ui-kit';
import styles from './index.module.css';
import { getFileUrlById } from '@onebase/platform-center';

type XCarouselConfig = typeof workbenchSchema.XCarouselWorkbench.config;

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

  const handleImageClick = (image: string) => {
    if (!runtime) return;

    if (image.indexOf('data:') < 0) {
      window.open(getFileUrlById(image));
    } else {
      window.open(image);
    }
  };
  return (
    <div className={styles.carouselWrapper}>
      {label.display &&
      label.text && <div className={styles.title}>{label.text}</div>}

      <Carousel
        className={styles.carousel}
        autoPlay={{ 
          interval: interval * 1000
        }}
        style={{
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        indicatorPosition="bottom"
      >
        {carouselConfig.map((img, index) => (
          <div className={styles.imageWrapper} key={index} onClick={() => handleImageClick(img.image)}>
            <img className={styles.image} src={img.image.indexOf('data:') < 0 ? getFileUrlById(img.image) : img.image} />
            {/* <div className={styles.text}>{img.text}</div> */}
          </div>
        ))}
      </Carousel>
    </div>
  );
});

export default XCarousel;
