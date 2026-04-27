import { Image } from '@arco-design/mobile-react';
import { memo } from 'react';
import { workbenchSchema, STATUS_OPTIONS, STATUS_VALUES } from '@onebase/ui-kit';
import { getFileUrlById } from '@onebase/platform-center';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';


type XImageWorkbenchConfig = typeof workbenchSchema.XImageWorkbench.config;

const XImageWorkbench = memo((props: XImageWorkbenchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, status, fillStyle, maxHeight, runtime = true, imageConfig, jumpType, jumpPageId, jumpExternalUrl } = props;
  const { handleJump } = useJump();
  const handleImgClick = () => {
    if (!runtime) return;
    if (jumpType === 'internal') {
      handleJump({
        menuUuid: jumpPageId,
        linkAddress: undefined,
        runtime
      });
    } else {
      handleJump({
        linkAddress: jumpExternalUrl,
        menuUuid: undefined,
        runtime
      });
    }
  }

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
        src={getFileUrlById(imageConfig)}
        style={
          {
            objectFit: fillStyle,
            maxHeight: maxHeight,
            borderRadius: 8,
            opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
          } as React.CSSProperties
        }
        onClick={handleImgClick}
      />
    </div>
    
  );
});

export default XImageWorkbench;
