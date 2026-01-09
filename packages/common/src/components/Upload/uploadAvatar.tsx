import { Avatar, Button, Image } from '@arco-design/web-react';
import { useRef } from 'react';
import { getCorpResourceById } from 'src/utils';
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
  const uploadRef = useRef(null);
  return (
    <div>
      {avatarUrl ? (
        <Image
          width={size.width}
          height={size.height}
          src={getCorpResourceById(avatarUrl)}
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
              const uploadInstance = uploadRef.current;
              if (uploadInstance) {
                // 清理文件列表
                try {
                  const instance = uploadInstance as any;
                  if (instance && typeof instance.clear === 'function') {
                    instance.clear();
                  }
                } catch (e) {
                  // 忽略错误
                }

                // 重置 input 的值，确保每次点击都能触发 change 事件
                const input = (uploadInstance as any)?.getRootDOMNode()?.querySelector('input[type="file"]');
                if (input) {
                  input.value = '';
                  // 使用 setTimeout 确保清理操作完成后再触发点击
                  setTimeout(() => {
                    input.click();
                  }, 50);
                }
              }
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
