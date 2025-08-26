import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Image } from '@arco-design/web-react';
import { memo } from 'react';
import { type XImageConfig } from './schema';

const XImage = memo((props: XImageConfig) => {
  const { status } = props;

  return (
    <Image
      width={'100%'}
      preview={false}
      src="//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/a8c8cdb109cb051163646151a4a5083b.png~tplv-uwbnlip3yd-webp.webp"
      alt="lamp"
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    />
  );
});

export default XImage;
