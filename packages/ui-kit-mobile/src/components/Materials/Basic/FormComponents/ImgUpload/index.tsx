import { Uploader, Progress, Toast, ImagePreview, Loading } from '@arco-design/mobile-react';
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
  url?: string;
  status?: "loaded" | "loading" | "error";
  file?: File;
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

  const [filesList, setFilesList] = useState<FileItem[]>([]);

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
  const renderUploadList = (fileListMethods) => {
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]) {
      return (
        <div className="uplaodList-text">
          {filesList.map(({ file, status, url }, index) => (
            <div key={index} className="uplaodList-text-item">
              <img className="uplaodList-text-item-img" src={file.url} alt="" />
              <div className="uplaodList-text-item-name">{file.name}</div>
              {status && status !== 'loaded' ? (
                <div className="uplaodList-text-item-process">
                  <Loading type="circle" radius={7} />
                  <IconClose
                    className="uplaodList-text-item-process-close"
                    onClick={() => fileListMethods.deleteFile(index)}
                  />
                </div>
              ) : (
                <div className="uplaodList-text-item-opera">
                  <span
                    onClick={() => {
                      if (url) {
                        ImagePreview.open({
                          openIndex: 0,
                          images: [{src: url}]
                        });
                      }
                    }}
                  >
                    <IconEyeVisible />
                  </span>
                  <IconDownload
                    onClick={() => {
                      if (url && file?.name) {
                        // downloadFileByUrl(file.url, file.name);
                      }
                    }}
                  />
                  <IconDelete
                    onClick={() => fileListMethods.deleteFile(index)}
                  />
                </div>
              )}
            </div>
          ))}
        </div >
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]) {
      return (
        <div className="uplaodList-list">
          {filesList.map(({ file, status, url }, index) => (
            <div key={index} className="uplaodList-list-item-wrapper">
              <div className="uplaodList-list-item">
                <img className="uplaodList-list-item-img" src={url} alt="" />
                <div className="uplaodList-list-item-content">
                  <div className="uplaodList-list-item-name">{file?.name}</div>
                  {/* <div className="uplaodList-list-item-size">
                    {file?.originFile?.size ? <span>{(file.originFile.size / 1024 / 1024).toFixed(2)}MB</span> : null}
                  </div> */}
                </div>
                <IconClose
                  className="uplaodList-text-item-process-close"
                  onClick={() => fileListMethods.deleteFile(index)}
                />
              </div>
              {status && status !== 'loaded' && <Loading type="circle" radius={7} />}
            </div>
          ))}
        </div>
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD]) {
      return (
        <div className="uplaodList-card">
          {filesList.map(({ file, status, url }, index) => (
            <div key={index} className="uplaodList-card-item">
              <div className="uplaodList-card-item-img">
                <img src={url} alt="" />
              </div>
              <div className="uplaodList-card-item-name">{file?.name}</div>
              <div className="uplaodList-card-item-footer">
                {/* <div className="uplaodList-card-item-size">
                  {file?.originFile?.size ? <span>{(file.originFile.size / 1024 / 1024).toFixed(2)}MB</span> : null}
                </div> */}
                <IconClose
                  className="uplaodList-text-item-process-close"
                  onClick={() => fileListMethods.deleteFile(index)}
                />
              </div>
            </div>
          ))}
        </div>
      );
    }
    return <></>;
  };

  const renderUploadArea = () => {
    return (
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
    )
  }

  return (
    <div className="formWrapper">
      {/* TODO 预览态下显示情况，及上传接口调用需要修改 */}
      <Uploader
        accept="image/*"
        files={filesList}
        limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
        onMaxSizeExceed={(file) =>
          Toast.toast({
            content: '文件大小超出限制',
            duration: 2000
          })}
        disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode}
        style={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        renderFileList={renderUploadList}
        renderUploadArea={renderUploadArea}
      />
    </div>
  );
});

export default XImgUpload;
