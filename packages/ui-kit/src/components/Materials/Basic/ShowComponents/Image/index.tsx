import { Image } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import './index.css';
import { type XImageConfig } from './schema';

const XImage = memo((props: XImageConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true, imageConfig } = props;

  return (
    <Image
      className="formWrapper imageStyle"
      width={'100%'}
      height={300}
      preview={runtime}
      src={imageConfig}
      style={
        {
          '--fit': fillStyle,
          '--maxHeight': maxHeight,
          borderRadius: 8,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        } as React.CSSProperties
      }
    />
  );
});

export default XImage;
