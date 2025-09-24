import { memo } from 'react';
import { Image } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XImageConfig } from './schema';
import '../index.css';
import './index.css';

const XImage = memo((props: XImageConfig & { runtime?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true } = props;

  return (
    <Image
      className='formWrapper imageStyle'
      width={'100%'}
      preview={runtime}
      src="https://devops.cm-iov.com:9000/system-static/img/annual2.jpg"// https://devops.cm-iov.com/static/img/bg.dd06daaa.png
      alt="lamp"
      style={{
        '--fit': fillStyle,
        '--maxHeight': maxHeight,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
      } as React.CSSProperties}
    />
  );
});

export default XImage;
