import { Form, Message, Upload } from '@arco-design/web-react';
import { IconPlus, IconDelete, IconImage } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, UPLOAD_VALUES, UPLOAD_OPTIONS } from '../../../constants';
import './index.css';
import type { XInputImgUploadConfig } from './schema';

const XImgUpload = memo((props: XInputImgUploadConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    listType,
    verify,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode
  } = props;

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
  const { form } = Form.useFormContext();

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    let flag = false;
    const newFieldValue = (fieldValue || []).map((ele: any) => {
      if (ele.url !== ele.response) {
        flag = true;
        return { ...ele, url: ele.response };
      }
      return { ...ele };
    });
    if (flag) {
      form.setFieldValue(fieldId, newFieldValue);
    }
  }, [fieldValue]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={fieldId}
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        triggerPropName="fileList"
      >
        <Upload
          imagePreview
          limit={
            (status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode) && fieldValue
              ? fieldValue?.length
              : verify?.maxCount === -1
                ? undefined
                : verify?.maxCount
          }
          accept="image/*"
          listType={listType}
          beforeUpload={async (file) => {
            const fileSizeLimit = verify?.maxSize * 1024; // 转换为kb;
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
          showUploadList={{
            removeIcon: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? null : <IconDelete />
          }}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
          drag
        >
          <div className="uplaodTrigger">
            {listType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && (
              <div className="uplaodTriggerText">
                <div className="uplaodTriggerText-content">
                  <IconImage />
                  <span className='uplaodTriggerText-tips'>图片上传</span>
                </div>
              </div>
            )}
            {listType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST] && (
              <div className="uplaodTriggerList">
                <div className="uplaodTriggerList-content">
                  <IconPlus />
                  <div className="uplaodTriggerList-tips">点击或拖拽文件到此处上传</div>
                  <div className="uplaodTriggerList-describe">单张图片大小上限{verify?.maxSize || 10}MB</div>
                </div>
              </div>
            )}
            {listType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD] && (
              <div className="uplaodTriggerPicture">
                <div className="uplaodTriggerPicture-content">
                  <IconImage />
                  <div className='uplaodTriggerPicture-tips'>图片上传</div>
                </div>
              </div>
            )}
          </div>
        </Upload>
      </Form.Item>
    </div>
  );
});

export default XImgUpload;