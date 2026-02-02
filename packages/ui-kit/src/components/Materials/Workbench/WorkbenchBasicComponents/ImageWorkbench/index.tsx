import { Image } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XImageConfig } from './schema';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.css';

const XImageWorkbench = memo((props: XImageConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, status, fillStyle, maxHeight, runtime = true, imageConfig } = props;

  return (
    <div className={styles.containerStyle}>
      <div className={styles.imageHeader}>
        {label?.display ? (
          <span className={styles.imageHeaderTitle}>{label?.text}</span>
        ) : null}
      </div>

      <Image
        className={styles.imageStyle}
        width={'100%'}
        height={300}
        preview={runtime}
        src={getFileUrlById(imageConfig)}
        style={
          {
            objectFit: fillStyle,
            maxHeight: maxHeight,
            borderRadius: 8,
            opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
          } as React.CSSProperties
        }
      />
    </div>
    
  );
});

export default XImageWorkbench;
