import { Message, Modal, Upload } from '@arco-design/web-react';
import { Cropper } from '../Cropper';
import { UploadSizeConfig } from './uploadAvatar';

interface IUploadProps {
    uploadRef: any;
    size?:UploadSizeConfig;
    aspect?: number;
    onUpdateUrl: (value: string) => void;
    getUploadFile: Function;
}

const UploadCommonComponent:React.FC<IUploadProps> = ({size, aspect, uploadRef, getUploadFile,onUpdateUrl}) => {
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

    const res = await getUploadFile(formData, progressAdapter);
    return res;
  };

    return  (
        <Upload
            ref={uploadRef}
            limit={1}
            accept="image/*"
            listType="picture-card"
            showUploadList={false}
            customRequest={async (option) => {
                const { onProgress, onError, onSuccess, file } = option;
                try {
                const uploadUrl = await handleUpload(file, onProgress);
                if (uploadUrl !== '') {
                    onUpdateUrl(uploadUrl);
                    onSuccess(uploadUrl);
                } else {
                    onError({
                    status: 'error',
                    msg: '上传失败'
                    });
                }
                } catch (error) {
                onError({
                    status: 'error',
                    msg: '上传失败'
                });
                }
            }}
            beforeUpload={(file) => {
                return new Promise((resolve) => {
                const modal = Modal.confirm({
                    title: '裁剪图片',
                    onCancel: () => {
                    Message.info('取消上传');
                    resolve(false);
                    modal.close();
                    },
                    simple: false,
                    content: (
                    <Cropper
                        aspect={aspect ? aspect : size?.aspect}
                        file={file}
                        onOK={(file: any) => {
                        resolve(file);
                        modal.close();
                        }}
                        onCancel={() => {
                        resolve(false);
                        Message.info('取消上传');
                        modal.close();
                        }}
                    />
                    ),
                    footer: null
                });
                });
            }}
            style={{
                display: 'none'
            }}
        ></Upload>
    )
}

export { UploadCommonComponent };