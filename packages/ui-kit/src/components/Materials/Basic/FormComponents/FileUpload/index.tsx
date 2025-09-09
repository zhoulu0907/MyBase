import { Form, Message, Upload } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { memo, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputFileUploadConfig } from './schema';
import '../index.css';

const XFileUpload = memo((props: XInputFileUploadConfig & { runtime?: boolean }) => {
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
    // description,
    runtime = true
  } = props;

  const [_fileUrl, setFileUrl] = useState<string>('');

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
      rules={[{ required: verify?.required }]}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
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
      >
        {listType == 'picture-card' && (
          <div className="arco-upload-trigger-picture">
            <div className="arco-upload-trigger-picture-text">
              <IconPlus />
              <div style={{ marginTop: 10, fontWeight: 600, fontSize: '11px' }}>点击或拖动文件到框内上传</div>
              <div style={{ marginTop: 5, fontWeight: 600, fontSize: '11px' }}>文件大小不超过{verify.maxSize}MB</div>
            </div>
          </div>
        )}
      </Upload>
    </Form.Item>
  );
});

export default XFileUpload;
