import { memo } from 'react';
import { Image } from '@arco-design/mobile-react';
import { STATUS_OPTIONS, STATUS_VALUES, ShowSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css';

const LoadFailPlaceholder = () => {
  return (
    <svg width="32" height="31" viewBox="0 0 32 31" fill="none">
      <path fillRule="evenodd" clipRule="evenodd" d="M26.9964 2.20298C26.9468 1.53032 26.3853 1 25.7 1H2.3L2.20298 1.00357C1.53032 1.05319 1 1.61466 1 2.3V25.7L1.00357 25.797C1.05319 26.4697 1.61466 27 2.3 27H18.252C18.0875 26.3608 18 25.6906 18 25H3V3H25V17.0619C25.3276 17.021 25.6613 17 26 17C26.3387 17 26.6724 17.021 27 17.0619V2.3L26.9964 2.20298ZM21.9194 18.1175V12.0594C21.9194 11.9255 21.8663 11.7971 21.7716 11.7024C21.5745 11.5053 21.2548 11.5053 21.0577 11.7024L14.6748 18.0846L12.2685 15.6781C12.0713 15.481 11.7517 15.481 11.5546 15.6781L6.33889 20.8938C6.26811 20.9645 6.22824 21.0605 6.228 21.1606C6.22749 21.3697 6.39659 21.5396 6.60569 21.5401L12.0044 21.5539C12.0368 21.563 12.071 21.5678 12.1063 21.5678H18.7716C19.456 20.129 20.5573 18.9268 21.9194 18.1175ZM10.5664 10.2118V6.42578H6.78034V10.2118H10.5664Z" fill="#C9CDD4" />
      <path d="M26 31C29.3137 31 32 28.3137 32 25C32 21.6863 29.3137 19 26 19C22.6863 19 20 21.6863 20 25C20 28.3137 22.6863 31 26 31Z" fill="#C9CDD4" />
      <path fillRule="evenodd" clipRule="evenodd" d="M28.7987 27.0083C28.8768 26.9302 28.8768 26.8036 28.7987 26.7255L27.0179 24.9447L28.7861 23.1764C28.8642 23.0983 28.8642 22.9717 28.7861 22.8936L28.0083 22.1157C27.9302 22.0376 27.8036 22.0376 27.7255 22.1157L25.9572 23.884L24.1318 22.0586C24.0537 21.9805 23.9271 21.9805 23.849 22.0586L23.0712 22.8364C22.9931 22.9145 22.9931 23.0411 23.0712 23.1192L24.8966 24.9447L23.0586 26.7827C22.9805 26.8608 22.9805 26.9874 23.0586 27.0655L23.8364 27.8433C23.9145 27.9214 24.0411 27.9214 24.1192 27.8433L25.9572 26.0053L27.7381 27.7861C27.8162 27.8642 27.9428 27.8642 28.0209 27.7861L28.7987 27.0083Z" fill="white" />
    </svg>
  );
}

const XImage = memo((props: ShowSchema.XImageConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, fillStyle, maxHeight, runtime = true, imageConfig, detailMode } = props;

  return (
    <Image
      className="formWrapper imageStyle"
      width='100%'
      height={300}
      showLoading={true}
      showError={true}
      src={imageConfig}
      radius={8}
      bordered
      fit={fillStyle}
      errorArea={<LoadFailPlaceholder />}
      style={
        {
          '--maxHeight': maxHeight,
          borderRadius: '0.16rem',
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          pointerEvents: 'none'
        } as React.CSSProperties
      }
    />
  );
});

export default XImage;
