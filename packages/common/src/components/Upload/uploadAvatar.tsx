import { Avatar, Button, Image } from '@arco-design/web-react';
import { useRef } from 'react';
import { getResourceURL } from 'src/utils';
import { UploadCommonComponent } from './upload';

export type UploadSizeConfig = {
  width: number;
  height?: number;
  aspect?: number;
};

interface IUploadComponentProps {
  avatarUrl: string;
  defaultPlaceholder: string;
  size?: UploadSizeConfig;
  defaultAvatarSize?: number;
  buttonName?: string;
  onUpdateUrl: (value: string) => void;
  getUploadFile: Function;
  footer?: React.ReactNode;
}

const UploadAvatarComponent: React.FC<IUploadComponentProps> = ({
  avatarUrl,
  defaultPlaceholder,
  size = {
    width: 120,
    height: 120,
    aspect: 1 / 1
  },
  defaultAvatarSize = 96,
  buttonName = '上传头像',
  onUpdateUrl,
  getUploadFile,
  footer
}) => {
  const getFileUrl = (resourceId: string) => {
    const resourceUrl = getResourceURL();
    return `${resourceUrl}/${resourceId}`;
  };

  const uploadRef = useRef(null);
  return (
    <div>
      {avatarUrl ? (
        <Image
          width={size.width}
          height={size.height}
          src={getFileUrl(avatarUrl)}
          alt="头像"
          style={{
            width: size.width,
            height: size.height,
            borderRadius: '50%',
            objectFit: 'cover',
            marginBottom: 16,
            display: 'block'
          }}
        />
      ) : (
        <Avatar size={defaultAvatarSize} style={{ marginBottom: '12px', backgroundColor: '#009e9e' }}>
          {defaultPlaceholder}
        </Avatar>
      )}
      <div>
        <UploadCommonComponent
          size={size}
          getUploadFile={getUploadFile}
          onUpdateUrl={onUpdateUrl}
          uploadRef={uploadRef}
        />
        {footer ? (
          footer
        ) : (
          <Button
            type="outline"
            onClick={() => {
              (uploadRef.current as any)?.getRootDOMNode()?.querySelector('input[type="file"]').click();
            }}
          >
            {buttonName}
          </Button>
        )}
      </div>
    </div>
  );
};

export { UploadAvatarComponent };
