import { memo, useState } from 'react';
import { Form, Message, Upload } from '@arco-design/web-react';
import { uploadFile } from '@onebase/platform-center';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputFileUploadConfig } from './schema';
import './index.css';

const XFileUpload = memo((props: XInputFileUploadConfig) => {
  const {
    label,
    status,
    tooltip,
    // showPreview, // todo
    // showDownload, // todo
    listType,
    verify,
    layout,
    labelColSpan = 0,
    description,
  } = props;

  const [fileUrl, setFileUrl] = useState<string>('');

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
      label={label.display && label.text}
      layout={layout}
      tooltip={tooltip}
      rules={[{ required: verify.required }]}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Upload
        limit={verify.maxCount === -1 ? undefined : verify.maxCount}
        accept={verify.fileFormat}
        listType={listType}
        beforeUpload={async (file) => {
          const fileSizeLimit = verify.maxSize * 1024; // 转换为kb;
          const fileSize = file.size / 1024;

          if (fileSize > fileSizeLimit) {
            Message.warning('文件大小超出限制');
            return false;
          }
        }}
        customRequest={async (option) => {
          const { onProgress, onError, onSuccess, file } = option;
          try {
            const uploadFileUrl = await handleUpload(file, onProgress);
            if (uploadFileUrl !== '') {
              setFileUrl(uploadFileUrl);
              onSuccess(uploadFileUrl);
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
        style={{
          width: '100%'
        }}
      />
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XFileUpload;
