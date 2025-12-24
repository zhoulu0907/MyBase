import { Carousel } from '@arco-design/mobile-react';
import { memo } from 'react';
import { useParams } from 'react-router-dom';
import { workbenchSchema } from '@onebase/ui-kit';
import { useJump } from '../../hooks/useJump';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.css';

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

  const { handleJump } = useJump();

  const handleImageClick = (item: any) => {
    handleJump({
      menuUuid: item.linkType === 'internal' ? item.internalPageId : undefined,
      linkAddress: item.linkType === 'external' ? item.url : undefined,
      runtime
    });
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
        {carouselConfig.map((item, index) => (
          <div className={styles.imageWrapper} key={index} onClick={() => handleImageClick(item)}>
            <img className={styles.image} src={item.image.indexOf('data:') < 0 ? getFileUrlById(item.image) : item.image} />
            {/* <div className={styles.text}>{item.text}</div> */}
          </div>
        ))}
      </Carousel>
    </div>
  );
});

export default XCarousel;
