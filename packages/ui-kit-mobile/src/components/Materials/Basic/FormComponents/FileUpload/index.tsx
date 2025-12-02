import { Form, Uploader, Toast, Loading } from '@arco-design/mobile-react';
import { type UploadItem } from '@arco-design/mobile-react/lib/Upload';
import { IconDelete, IconClose, IconDownload, IconFile } from '@arco-design/mobile-react/esm/icon';
import { uploadFile } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';
import './index.css'

type XFileUploadConfig = typeof FormSchema.XFileUploadSchema.config;

// 定义文件项类型
interface FileItem {
  url?: string;
  status?: "loaded" | "loading" | "error";
  file?: File;
}

const XFileUpload = memo((props: XFileUploadConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
  const {
    label,
    dataField,
    status,
    verify,
    layout,
    runtime = true,
    detailMode,
    form
  } = props;

  const [filesList, setFilesList] = useState<FileItem[]>([]);

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`;

  const handleUpload = async ({ file }: { file: File }) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await uploadFile(formData);
      return {
        name: file.name,
        response: res
      };
    } catch (error) {
      Toast.toast({
        content: '上传失败，请重试',
        duration: 2000
      });
      throw error;
    }
  };

  const handleChange = (files: any[]) => {
    setFilesList(files);
    // 将文件数据同步到表单字段
    if (form) {
      // 提取需要保存到表单的数据，如文件名和URL
      const formValues = files.map(file => ({
        name: file.name,
        url: file.url || '',
        response: file.response
      }));
      form.setFieldValue(fieldId, formValues);
    }
  };

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
            {getFileIcon(file as UploadItem)}
            <div className="uplaodList-text-item-name">{file?.name}</div>
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
      </div>
    );
  };

  return (
    <div>
      <Form.Item
        className="inputTextWrapper fileUploadWrapper"
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
        <Uploader
          accept={verify?.fileFormat}
          files={filesList}
          limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
          onMaxSizeExceed={(file) =>
            Toast.toast({
              content: '文件大小超出限制',
              duration: 2000
            })}
          onLimitExceed={(file) =>
            Toast.toast({
              content: '文件数量超出限制',
              duration: 2000
            })}
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode}
          style={{
            width: '100%'
          }}
          renderFileList={renderUploadList}
          onChange={handleChange}
          upload={handleUpload}
        />
      </Form.Item>
    </div>
  );
});

export default XFileUpload;
