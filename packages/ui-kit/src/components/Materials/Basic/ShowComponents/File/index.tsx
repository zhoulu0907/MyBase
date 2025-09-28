import { memo } from 'react';
import { type XFileConfig } from './schema';
import { IconDownload } from '@arco-design/web-react/icon';
import './index.css';
import { downloadFileByUrl } from 'src/utils/downloadFile';

const XFile = memo((props: XFileConfig & { runtime?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true, fileConfig } = props;

  const downloadFile = async (item: any) => {
    downloadFileByUrl(item.url,item.name)
  }

  return (
    <div>
      {(!fileConfig || fileConfig.length == 0) && <span>静态文件</span>}
      {fileConfig?.map((item: any, index) => <div className='fileItem' key={index} onClick={() => downloadFile(item)}>
        <div className='fileItemName'>{item.name}</div>
        <IconDownload />
      </div>)}
    </div>
  );
});

export default XFile;
