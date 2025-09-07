import { memo, useState } from 'react';
import { nanoid } from 'nanoid';
import { Form, Message, Upload } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputImgUploadConfig } from './schema';
import '../index.css';

const XImgUpload = memo((props: XInputImgUploadConfig & { runtime?: boolean }) => {
  const { label, dataField, status, tooltip, listType, verify, layout, labelColSpan = 0, description, runtime = true } = props;

  const [_imgUrl, setImgUrl] = useState<string>('');

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
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`}
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }]}
      hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
      style={{
        margin: 0,
        padding: 6,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
      }}
    >
      <Upload
        imagePreview
        limit={verify.maxCount === -1 ? undefined : verify.maxCount}
        accept="image/*"
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
      >
        {listType == 'picture-card' && (
          <div className="arco-upload-trigger-picture">
            <div className="arco-upload-trigger-picture-text">
              <IconPlus />
              <div style={{ marginTop: 10, fontWeight: 600, fontSize: '11px' }}>点击或拖动图片到框内上传</div>
            </div>
          </div>
        )}
      </Upload>
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XImgUpload;
