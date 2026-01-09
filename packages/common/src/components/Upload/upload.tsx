import { Message, Modal, Upload } from '@arco-design/web-react';
import { useRef, useState } from 'react';
import { Cropper } from '../Cropper';
import { UploadSizeConfig } from './uploadAvatar';

interface IUploadProps {
  uploadRef: any;
  size?: UploadSizeConfig;
  aspect?: number;
  onUpdateUrl: (value: string) => void;
  getUploadFile: Function;
  imagePreview?: boolean;
}

const UploadCommonComponent: React.FC<IUploadProps> = ({ size, aspect, uploadRef, getUploadFile, onUpdateUrl }) => {
  const [fileList, setFileList] = useState<any[]>([]);
  const beforeUploadPromiseRef = useRef<Promise<any> | null>(null);

  const handleUpload = async (file: File, onProgress?: (percent: number, event?: ProgressEvent) => void) => {
    const formData = new FormData();
    formData.append('file', file);

    const progressAdapter = onProgress
      ? (progressEvent: ProgressEvent) => {
          if (progressEvent.lengthComputable) {
            const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            onProgress(percent, progressEvent);
          }
        }
      : undefined;

    try {
      const res = await getUploadFile(formData, progressAdapter);
      return res;
    } catch (error) {
      console.error('上传文件失败', error);
    }
  };

  // 清理文件列表
  const clearFileList = () => {
    setFileList([]);
    // 通过 ref 访问 Upload 组件的方法来清理
    if (uploadRef?.current) {
      try {
        const uploadInstance = uploadRef.current;
        if (uploadInstance && typeof uploadInstance.clear === 'function') {
          uploadInstance.clear();
        }
      } catch (e) {
        // 忽略错误
      }
    }
  };

  return (
    <Upload
      ref={uploadRef}
      limit={1}
      accept="image/*"
      listType="picture-card"
      showUploadList={false}
      fileList={fileList}
      onChange={(fileList) => {
        setFileList(fileList);
        // 如果文件列表为空，说明用户取消了上传
        if (fileList.length === 0) {
          beforeUploadPromiseRef.current = null;
        }
      }}
      customRequest={async (option) => {
        const { onProgress, onError, onSuccess, file } = option;
        try {
          const resourceId = await handleUpload(file, onProgress);

          if (resourceId && resourceId !== '') {
            onUpdateUrl(resourceId);
            onSuccess(resourceId);
            // 上传成功后清理文件列表，为下次上传做准备
          } else {
            onSuccess();
          }

          setTimeout(() => {
            clearFileList();
          }, 100);
        } catch (error) {
          onError({
            status: 'error',
            msg: '上传失败'
          });
        }
      }}
      beforeUpload={(file) => {
        // 如果之前有未完成的 Promise，先清理
        if (beforeUploadPromiseRef.current) {
          clearFileList();
        }

        const promise = new Promise((resolve) => {
          let resolved = false;
          const resolveOnce = (value: any) => {
            if (!resolved) {
              resolved = true;
              resolve(value);
              beforeUploadPromiseRef.current = null;
              // 如果返回 false，清理文件列表
              if (value === false) {
                setTimeout(() => {
                  clearFileList();
                }, 100);
              }
            }
          };

          const modal = Modal.confirm({
            title: '裁剪图片',
            onCancel: () => {
              if (!resolved) {
                Message.info('取消上传');
                resolveOnce(false);
              }
              modal.close();
            },
            onOk: () => {
              // 防止用户通过其他方式关闭 Modal
              if (!resolved) {
                resolveOnce(false);
              }
            },
            simple: false,
            content: (
              <Cropper
                aspect={aspect ? aspect : size?.aspect}
                file={file}
                onOK={(file: any) => {
                  resolveOnce(file);
                  modal.close();
                }}
                onCancel={() => {
                  if (!resolved) {
                    Message.info('取消上传');
                    resolveOnce(false);
                  }
                  modal.close();
                }}
              />
            ),
            footer: null
          });
        });

        beforeUploadPromiseRef.current = promise;
        return promise;
      }}
      style={{
        display: 'none'
      }}
    ></Upload>
  );
};

export { UploadCommonComponent };
