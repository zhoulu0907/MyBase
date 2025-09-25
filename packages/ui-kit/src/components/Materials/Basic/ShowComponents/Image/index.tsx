import { memo, useState } from 'react';
import { Image } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XImageConfig } from './schema';
import '../index.css';
import './index.css';

const XImage = memo((props: XImageConfig & { runtime?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true, imageConfig } = props;
  console.log('imageConfig:',imageConfig)
  return (
     <Image
        className='formWrapper imageStyle'
        width={'100%'}
        height={300}
        preview={runtime}
        src={imageConfig}
        style={{
          '--fit': fillStyle,
          '--maxHeight': maxHeight,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        } as React.CSSProperties}
      />
  );
});

export default XImage;
