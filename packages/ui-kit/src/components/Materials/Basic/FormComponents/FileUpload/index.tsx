import { Form, Message, Upload } from '@arco-design/web-react';
import { IconPlus, IconDelete } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useState, useEffect } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputFileUploadConfig } from './schema';

const XFileUpload = memo((props: XInputFileUploadConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    // showPreview, // todo
    // showDownload, // todo
    listType,
    verify,
    layout,
    labelColSpan = 0,
    runtime = true,
    detailMode
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

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form)

 useEffect(()=>{
    let flag = false;
    const newFieldValue=  (fieldValue || []).map((ele:any)=>{
      if(ele.url !== ele.response){
        flag = true;
        return {...ele,url:ele.response}
      }
      return {...ele}
    })
    if(flag){
      form.setFieldValue(fieldId,newFieldValue)
    }
  },[fieldValue])

  return (
    <div className="formWrapper">
      <Form.Item
        label={label.display && label.text}
        field={fieldId}
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        triggerPropName='fileList'
      >
        <Upload
          limit={(status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode) && fieldValue ? fieldValue?.length : (
            verify?.maxCount === -1 ? undefined : verify?.maxCount
          )}
          accept={verify?.fileFormat}
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
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
          showUploadList={{
            removeIcon: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? null : <IconDelete />
          }}
        >
          {listType == 'picture-card' && (
            <div className="arco-upload-trigger-picture">
              <div className="arco-upload-trigger-picture-text">
                <IconPlus />
                <div style={{ marginTop: 10, fontWeight: 600, fontSize: '11px' }}>点击或拖动文件到框内上传</div>
                <div style={{ marginTop: 5, fontWeight: 600, fontSize: '11px' }}>文件大小不超过{verify?.maxSize}MB</div>
              </div>
            </div>
          )}
        </Upload>
      </Form.Item>
    </div>
  );
});

export default XFileUpload;
