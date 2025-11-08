import { Uploader, Progress, Toast, ImagePreview } from '@arco-design/mobile-react';
import { IconDelete, IconClose, IconDownload, IconEyeVisible } from '@arco-design/mobile-react/esm/icon';
import { uploadFile } from '@onebase/platform-center';
// import { downloadFileByUrl } from 'src/utils/downloadFile';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, UPLOAD_VALUES, UPLOAD_OPTIONS } from '../../../constants';
import './index.css';
import type { XInputImgUploadConfig } from './schema';

// 定义文件项类型
interface FileItem {
  uid: string;
  url?: string;
  name?: string;
  percent?: number;
  status?: 'uploading' | 'done' | 'error';
  originFile?: File;
}

// 定义上传列表属性类型
interface UploadListProps {
  onRemove?: (file: FileItem) => void;
}

const XImgUpload = memo((props: XInputImgUploadConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    listType,
    uploadType,
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
  // const { form } = Form.useFormContext();

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;
  // const fieldValue = Form.useWatch(fieldId, form);

  // useEffect(() => {
  //   let flag = false;
  //   const newFieldValue = (fieldValue || []).map((ele: any) => {
  //     if (ele.url !== ele.response) {
  //       flag = true;
  //       return { ...ele, url: ele.response };
  //     }
  //     return { ...ele };
  //   });
  //   if (flag) {
  //     form.setFieldValue(fieldId, newFieldValue);
  //   }
  // }, [fieldValue]);

  // 自定义文件列表展示
  const renderUploadList = (filesList: FileItem[], props: UploadListProps) => {
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]) {
      return (
        <div className="uplaodList-text">
          {filesList.map((file) => (
            <div key={file.uid} className="uplaodList-text-item">
              <img className="uplaodList-text-item-img" src={file.url} alt="" />
              <div className="uplaodList-text-item-name">{file.name}</div>
              {file.percent && file.percent !== 100 ? (
                <div className="uplaodList-text-item-process">
                  <Progress percentage={file.percent} />
                  <span
                    className="uplaodList-text-item-process-close"
                    onClick={() => {
                      if (props.onRemove) {
                        props.onRemove(file);
                      }
                    }}
                  >
                  <IconClose />
                  </span>
                </div>
              ) : (
                <div className="uplaodList-text-item-opera">
                  <span
                    onClick={() => {
                      if (file.url) {
                        ImagePreview.open({
                          images: [file.url]
                        });
                      }
                    }}
                  >
                  <IconEyeVisible />
                  </span>
                  <span
                    onClick={() => {
                      if (file.url && file.name) {
                        // downloadFileByUrl(file.url, file.name);
                      }
                    }}
                  >
                  <IconDownload />
                  </span>
                  <span
                    onClick={() => {
                      if (props.onRemove) {
                        props.onRemove(file);
                      }
                    }}
                  >
                  <IconDelete />
                  </span>
                </div>
              )}
            </div>
          ))}
        </div>
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]) {
      return (
        <div className="uplaodList-list">
          {filesList.map((file) => (
            <div key={file.uid} className="uplaodList-list-item-wrapper">
              <div className="uplaodList-list-item">
                <img className="uplaodList-list-item-img" src={file.url} alt="" />
                <div className="uplaodList-list-item-content">
                  <div className="uplaodList-list-item-name">{file.name}</div>
                  <div className="uplaodList-list-item-size">
                    {file?.originFile?.size ? <span>{(file.originFile.size / 1024 / 1024).toFixed(2)}MB</span> : null}
                  </div>
                </div>
                <span
                  className="uplaodList-list-item-close"
                  onClick={() => {
                    if (props.onRemove) {
                      props.onRemove(file);
                    }
                  }}
                >
                <IconClose />
                </span>
              </div>
              {file.percent && file.percent !== 100 ? <Progress percentage={file.percent} /> : null}
            </div>
          ))}
        </div>
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD]) {
      return (
        <div className="uplaodList-card">
          {filesList.map((file) => (
            <div key={file.uid} className="uplaodList-card-item">
              <div className="uplaodList-card-item-img">
                <img src={file.url} alt="" />
              </div>
              <div className="uplaodList-card-item-name">{file.name}</div>
              <div className="uplaodList-card-item-footer">
                <div className="uplaodList-card-item-size">
                  {file?.originFile?.size ? <span>{(file.originFile.size / 1024 / 1024).toFixed(2)}MB</span> : null}
                </div>
                <span
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    if (props.onRemove) {
                      props.onRemove(file);
                    }
                  }}
                >
                <IconDelete />
                </span>
              </div>
            </div>
          ))}
        </div>
      );
    }
    return <></>;
  };

  return (
    <div className="formWrapper">
      {/* TODO 预览态下显示情况，及上传接口调用需要修改 */}
      <Uploader
        accept="image/*"
        limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
        beforeUpload={async (file: File) => {
          const fileSizeLimit = verify?.maxSize * 1024; // 转换为kb;
          const fileSize = file.size / 1024;
          if (fileSize > fileSizeLimit) {
            Toast.toast({
              content: '文件大小超出限制',
              duration: 2000
            });
            return false;
          }
          return true;
        }}
        customRequest={async (option: {
          onProgress?: (percent: number) => void;
          onError: (error: { status: string; msg: string }) => void;
          onSuccess: (result: { url: string; name?: string }) => void;
          file: File;
        }) => {
          const { onProgress, onError, onSuccess, file } = option;
          try {
            const uploadImgUrl = await handleUpload(file, onProgress);
            if (uploadImgUrl !== '') {
              setImgUrl(uploadImgUrl);
              onSuccess({
                url: uploadImgUrl,
                name: file.name
              });
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
        disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode}
        style={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        renderUploadList={renderUploadList}
      >
        <div className="uplaodTrigger">
          {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && (
            <div className="uplaodTriggerText">
              <div className="uplaodTriggerText-content">
                <span></span>
                <span className="uplaodTriggerText-tips">图片上传</span>
              </div>
            </div>
          )}
          {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST] && (
            <div className="uplaodTriggerList">
              <div className="uplaodTriggerList-content">
                <div className="uplaodTriggerList-tips">点击或拖拽文件到此处上传</div>
                <div className="uplaodTriggerList-describe">
                  最多可上传{verify?.maxCount && verify?.maxCount > 0 ? verify?.maxCount : 1}
                  张图片，单张图片大小不超过{verify?.maxSize || 10}MB
                </div>
              </div>
            </div>
          )}
          {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD] && (
            <div className="uplaodTriggerPicture">
              <div className="uplaodTriggerPicture-content">
                <div className="uplaodTriggerPicture-tips">图片上传</div>
              </div>
            </div>
          )}
        </div>
      </Uploader>


      {/* <Form.Item
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
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT]}
          drag={uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]}
          renderUploadList={renderUploadList}
        >
          <div className="uplaodTrigger">
            {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && (
              <div className="uplaodTriggerText">
                <div className="uplaodTriggerText-content">
                  <IconImage />
                  <span className="uplaodTriggerText-tips">图片上传</span>
                </div>
              </div>
            )}
            {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST] && (
              <div className="uplaodTriggerList">
                <div className="uplaodTriggerList-content">
                  <IconPlus />
                  <div className="uplaodTriggerList-tips">点击或拖拽文件到此处上传</div>
                  <div className="uplaodTriggerList-describe">
                    最多可上传{verify?.maxCount && verify?.maxCount > 0 ? verify?.maxCount : 1}
                    张图片，单张图片大小不超过{verify?.maxSize || 10}MB
                  </div>
                </div>
              </div>
            )}
            {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD] && (
              <div className="uplaodTriggerPicture">
                <div className="uplaodTriggerPicture-content">
                  <IconImage />
                  <div className="uplaodTriggerPicture-tips">图片上传</div>
                </div>
              </div>
            )}
          </div>
        </Upload>
      </Form.Item> */}
    </div>
  );
});

export default XImgUpload;
