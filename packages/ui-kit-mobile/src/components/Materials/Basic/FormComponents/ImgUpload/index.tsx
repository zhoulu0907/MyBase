import { memo } from 'react';
import { nanoid } from 'nanoid';
import { Toast, ImagePicker, Form } from '@arco-design/mobile-react';
import { uploadFile } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import './index.css';

type XImgUploadConfig = typeof FormSchema.XImgUploadSchema.config;

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

const XImgUpload = memo((props: XImgUploadConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    label,
    dataField,
    status,
    listType,
    uploadType,
    verify,
    layout,
    runtime = true,
    detailMode,
  } = props;

  const handleUpload = async ({ file }: { file: File }) => {
    const formData = new FormData();
    formData.append('file', file);

    const res = await uploadFile(formData);
    return {
      name: file.name,
      response: res
    };
  };

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;

  return (
    <div>
      <Form.Item
        className="inputTextWrapperOBMobile ImgUploadWrapperOBMobile"
        label={
          label.display && label.text
        }
        layout="vertical"
        field={fieldId}
        required={verify?.required}
        trigger="fileList"
        style={{
          pointerEvents: runtime ? 'unset' : 'none',
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <ImagePicker
          accept="image/*"
          limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
          maxSize={verify.maxSize * 1024}
          upload={handleUpload}
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
            width: '100%'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XImgUpload;
