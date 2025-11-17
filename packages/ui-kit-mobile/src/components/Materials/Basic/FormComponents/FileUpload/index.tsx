import { Form, Uploader, CircleProgress, Toast, Loading } from '@arco-design/mobile-react';
import { type UploadItem, type UploadListProps } from '@arco-design/mobile-react/lib/Upload';
import { IconDelete, IconClose, IconDownload, IconFile, IconAdd } from '@arco-design/mobile-react/esm/icon';
import { uploadFile } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useState, useEffect } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
// import { downloadFileByUrl } from 'src/utils/downloadFile';
import '../index.css';
import './index.css'
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

  const [filesList, setFilesList] = useState<{file: File, status: "loaded" | "loading" | "error", url: string}[]>([]);

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
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`;
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
        {filesList.map(({ file, status, url }, index) => (
          <div key={index} className="uplaodList-text-item">
            {getFileIcon(file)}
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
                <IconDownload
                  onClick={() => {
                    if (url && file.name) {
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
      </div>
    );
  };

  return (
    <div className="formWrapper fileUploadWrapper">
      {/* TODO 预览态下显示情况，及上传接口调用需要修改 */}
      <Uploader
        files={filesList}
        limit={
          (status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode) ? 1 : verify?.maxCount === -1 ? undefined : verify?.maxCount
        }
        accept={verify?.fileFormat}
        // onChange={setFilesList} 
        onMaxSizeExceed={(file) =>         
          Toast.toast({
            content: '文件大小超出限制',
            duration: 2000
        })}
        style={{
          width: '100%',
          pointerEvents: runtime ? 'unset' : 'none'
        }}
        renderFileList={renderUploadList}
      />
    </div>
  );
});

export default XFileUpload;
