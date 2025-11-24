import { Uploader, Progress, Toast, ImagePreview, Loading, ImagePicker, Form } from '@arco-design/mobile-react';
import { IconDelete, IconClose, IconDownload, IconEyeVisible } from '@arco-design/mobile-react/esm/icon';
import { uploadFile } from '@onebase/platform-center';
// import { downloadFileByUrl } from 'src/utils/downloadFile';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
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

const XImgUpload = memo((props: XInputImgUploadConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
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
    detailMode,
    form
  } = props;

  const [filesList, setFilesList] = useState<FileItem[]>([]);

  const handleUpload = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);

    // const progressAdapter = onProgress
    //   ? (progressEvent: ProgressEvent) => {
    //     if (progressEvent.lengthComputable) {
    //       const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
    //       onProgress(percent, progressEvent);
    //     }
    //   }
    //   : undefined;

    const res = await uploadFile(formData);
    return res;
  };

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;

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

  // const [images, setImages] = useState([]);

  // console.warn(form.getFieldValue(fieldId));

  return (
    <div>
      <Form.Item
        className="inputTextWrapper"
        label={
          label.display && label.text
        }
        layout="vertical"
        field={fieldId}
        required={verify?.required}
        trigger="fileList"
        // initialValue={images}
        style={{
          // margin: 0,
          // opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <ImagePicker
          accept="image/*"
          limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
          // images={images}
          maxSize={verify.maxSize * 1024}
          upload={handleUpload}
          // onChange={setImages}
          onMaxSizeExceed={(file) => {
            Toast.toast({
              content: '文件大小超出限制',
              duration: 2000
            })
          }}
          onLimitExceed={(file) =>
            Toast.toast({
              content: '文件数量超出限制',
              duration: 2000
            })}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>

      {/* TODO 预览态下显示情况，及上传接口调用需要修改 */}



      {/* <Uploader
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
      /> */}
    </div>
  );
});

export default XImgUpload;
