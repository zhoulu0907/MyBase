import { memo } from 'react';
import { IconDownload } from '@arco-design/mobile-react/esm/icon';
import { downloadFileByUrl, ShowSchema, STATUS_OPTIONS, STATUS_VALUES } from '@onebase/ui-kit';
import styles from './index.module.css';

type XFileConfig = typeof ShowSchema.XFileSchema.config;

const XFile = memo((props: XFileConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true, fileConfig } = props;

  const downloadFile = async (item: any) => {
    downloadFileByUrl(item.url, item.name);
  };

  return (
    <div
      style={
        {
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          pointerEvents: 'none',
          padding: '0.32rem'
        } as React.CSSProperties
      }>
      {(!fileConfig || fileConfig.length == 0) && <span>静态文件</span>}
      {fileConfig?.map((item: any, index: number) => (
        <div className={styles.fileItem} key={index} onClick={() => downloadFile(item)}>
          <div className={styles.fileItemName}>{item.name}</div>
          <IconDownload className={styles.fileItemIcon} />
        </div>
      ))}
    </div>
  );
});

export default XFile;
