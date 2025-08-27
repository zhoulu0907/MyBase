import { Form, Message, Upload } from '@arco-design/web-react';
import { uploadFile } from '@onebase/platform-center';
import { memo, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputImgUploadConfig } from './schema';

const XImgUpload = memo((props: XInputImgUploadConfig) => {
  const { label, dataField, status, tooltip, uploadSize = 10, listType, required, layout, labelColSpan = 0 } = props;

  const [imgUrl, setImgUrl] = useState<string>('');

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

    const res = await uploadFile(formData, progressAdapter);
    return res;
  };

  return (
    <Form.Item
      label={label}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : ''}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Upload
        imagePreview
        limit={1}
        accept="image/*"
        listType={listType}
        beforeUpload={async (file) => {
          const fileSizeLimit = uploadSize * 1024; // 转换为kb;
          const fileSize = file.size / 1024;

          if (fileSize > fileSizeLimit) {
            Message.warning('文件大小超出限制');
            return false;
          }
        }}
        customRequest={async (option) => {
          const { onProgress, onError, onSuccess, file } = option;
          try {
            const uploadImgUrl = await handleUpload(file, onProgress);
            if (uploadImgUrl !== '') {
              setImgUrl(uploadImgUrl);
              onSuccess(uploadImgUrl);
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
        showUploadList
        style={{
          width: '100%'
        }}
      />
    </Form.Item>
  );
});

export default XImgUpload;
