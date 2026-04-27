import { Image } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XImageConfig } from './schema';
import { getFileUrlById } from '@onebase/platform-center';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';

const XImageWorkbench = memo((props: XImageConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, status, fillStyle, runtime = true, imageConfig, jumpType, jumpPageId, jumpExternalUrl } = props;
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
      <div className={styles.imageHeader} style={label?.display ? {marginBottom: '12px'} : {}}>
        {label?.display ? (
          <span className={styles.imageHeaderTitle}>{label?.text}</span>
        ) : null}
      </div>

      <Image
        className={styles.imageStyle}
        height='300px'
        width='100%'
        preview={false}
        src={getFileUrlById(imageConfig)}
        style={
          {
            '--fit': fillStyle,
            opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
          } as React.CSSProperties
        }
        onClick={handleImgClick}
      />
    </div>
    
  );
});

export default XImageWorkbench;
