import { Form, Message, Upload, Progress, Button } from '@arco-design/web-react';
import { type UploadItem, type UploadListProps } from '@arco-design/web-react/lib/Upload';
import { IconPlus, IconDelete, IconClose, IconDownload, IconFile, IconUpload } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useState, useEffect } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, UPLOAD_VALUES, UPLOAD_OPTIONS } from '../../../constants';
import { downloadFileByUrl } from 'src/utils/downloadFile';
import '../index.css';
import type { XInputFileUploadConfig } from './schema';

const XFileUpload = memo((props: XInputFileUploadConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    showDownload,
    buttonName,
    buttonType,
    uploadType,
    verify,
    layout,
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
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`;
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

  // 自定义文件列表展示
  const renderUploadList = (filesList: UploadItem[], props: UploadListProps) => {
    const getFileIcon = (file: UploadItem) => {
      if (file?.name) {
        // todo  根据文件类型展示不同icon
        const index = file.name.lastIndexOf('.');
        const type = file.name.slice(index + 1)
      }
      return <IconFile style={{ fontSize: '40px' }} />
    }
    return (
      <div className="uplaodList-text">
        {filesList.map((file) => (
          <div key={file.uid} className="uplaodList-text-item">
            {getFileIcon(file)}
            <div className="uplaodList-text-item-name">{file.name}</div>
            {file.percent && file.percent !== 100 ? (
              <div className="uplaodList-text-item-process">
                <Progress color="rgb(var(--primary-7))" percent={file.percent} showText={false}></Progress>
                <IconClose
                  className="uplaodList-text-item-process-close"
                  onClick={() => {
                    if (props.onRemove) {
                      props.onRemove(file);
                    }
                  }}
                />
              </div>
            ) : (
              <div className="uplaodList-text-item-opera">
                {showDownload && <IconDownload
                  onClick={() => {
                    if (file.url && file.name) {
                      downloadFileByUrl(file.url, file.name);
                    }
                  }}
                />}

                {!detailMode && <IconDelete
                  onClick={() => {
                    if (props.onRemove) {
                      props.onRemove(file);
                    }
                  }}
                />}
              </div>
            )}
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        wrapperCol={{ style: { flex: 1 } }}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        triggerPropName="fileList"
      >
        <Upload
          limit={
            (status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode) && fieldValue
              ? fieldValue?.length
              : verify?.maxCount === -1
                ? undefined
                : verify?.maxCount
          }
          accept={verify?.fileFormat}
          listType='text'
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
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || detailMode}
          showUploadList={{
            removeIcon: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? null : <IconDelete />
          }}
          renderUploadList={renderUploadList}
        >
          {!detailMode && (
            <div className="uplaodTrigger">
              {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && (
                <Button type={buttonType || 'primary'} >
                  <IconUpload />
                  <span>{buttonName || '点击上传'}</span>
                </Button>
              )}
              {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST] && (
                <div className="uplaodTriggerList">
                  <div className="uplaodTriggerList-content">
                    <IconUpload />
                    <div className="uplaodTriggerList-tips">{buttonName || '点击或拖拽文件到此处上传'}</div>
                    <div className="uplaodTriggerList-describe">支持{verify?.fileFormat}格式</div>
                    <div className="uplaodTriggerList-describe">
                      最多上传{verify?.maxCount && verify?.maxCount > 0 ? verify?.maxCount : 1}
                      个文件，单个文件不超过{verify?.maxSize || 10}MB
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </Upload>
      </Form.Item>
    </div>
  );
});

export default XFileUpload;
