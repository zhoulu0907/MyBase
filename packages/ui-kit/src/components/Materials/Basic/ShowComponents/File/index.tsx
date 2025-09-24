import { memo } from 'react';
import { Space } from '@arco-design/web-react';
import { type XFileConfig } from './schema';
import { IconDownload } from '@arco-design/web-react/icon';

const XFile = memo((props: XFileConfig & { runtime?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true, fileCofig } = props;

  return (
    <Space>
      {(!fileCofig || fileCofig.length == 0) && <span>静态文件</span>}
      {fileCofig?.map((item:any)=><div>
        <div>{item.name}</div>
        <IconDownload />
      </div>)}
    </Space>
  );
});

export default XFile;
