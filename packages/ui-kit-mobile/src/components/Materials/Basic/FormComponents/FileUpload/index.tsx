import DownloadLink from '@/assets/images/download_link.svg';
import { Button, Ellipsis, Form, Loading, Popover, Toast, Uploader } from '@arco-design/mobile-react';
import { FileListMethods } from '@arco-design/mobile-react/cjs/uploader';
import { IconClose, IconDelete, IconDownload, IconQuestionCircle, IconUpload } from '@arco-design/mobile-react/esm/icon';
import { ITypeRules, ValidatorType } from '@arco-design/mobile-utils';
import { attachmentDownload, attachmentUpload, menuSignal } from '@onebase/app';
import { FORM_COMPONENT_TYPES, FormSchema, STATUS_OPTIONS, STATUS_VALUES, downloadFileByUrl } from '@onebase/ui-kit';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { CommonFileItem } from '@arco-design/mobile-react/cjs/uploader/upload/type';
import { UPLOAD_OPTIONS, UPLOAD_VALUES } from '@onebase/ui-kit/src/components/Materials/constants';
import '../index.css';
import './index.css';

type XFileUploadConfig = typeof FormSchema.XFileUploadSchema.config;

// 定义文件项类型
interface FileItem {
  url?: string;
  status?: 'loaded' | 'loading' | 'error';
  file?: File;
  name?: string;
  id?: string;
  type: string;
}

const UploadButtonType = {
  primary: 'primary',
  secondary: 'default',
  outline: 'ghost'
} as any;

const XFileUpload = memo(
  (props: XFileUploadConfig & { runtime?: boolean; detailMode?: boolean; recordId?: string; form?: any }) => {
    const { label, dataField, status, verify, layout, buttonName, buttonType, uploadType, runtime = true, detailMode, form, showDownload } = props;

    const [tableName, fieldName] = dataField;
    const { curMenu } = menuSignal;

    const [filesList, setFilesList] = useState<FileItem[]>([]);
    const fieldId =
      dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`;

    useEffect(() => {
      const fieldValue = form?.getFieldValue(fieldId);
      if (fieldValue && Array.isArray(fieldValue)) {
        setFilesList(fieldValue);
      }
    }, [form, fieldId]);

    const handleUpload = async (files: CommonFileItem): Promise<CommonFileItem | null> => {
      if (!files.file) {
        return null;
      }

      try {
        const formData = new FormData();
        formData.append('file', files.file);

        if (runtime) {
          const res = await attachmentUpload(tableName, formData);
          return {
            id: res,
            url: files.url,
            name: files.file.name || '',
          } as CommonFileItem;
        }
        return null;
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
        const formValues = files.map((file) => ({
          name: file.name,
          url: file.url || '',
          id: file.id
        }));
        form.setFieldValue(fieldId, formValues);
      }
    };

    // 自定义文件列表展示
    const renderUploadList = (fileListMethods: FileListMethods) => {
      const getFileIcon = (file: any) => {
        if (file?.name) {
          // todo  根据文件类型展示不同icon
          const index = file.name.lastIndexOf('.');
          const type = file.name.slice(index + 1);
        }
        return <img src={DownloadLink} alt="download_link" />;
      };

      return (
        <div className="uplaodList-text">
          {filesList.map(({ id, type, status, url, name }, index) => (
            <div key={index} className="uplaodList-text-item">
              {getFileIcon(type as any)}
              <div className="uplaodList-text-item-name">{name}</div>
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
                  {showDownload && (
                    <IconDownload
                      style={{ color: 'rgb(var(--primary-6))' }}
                      onClick={async (e) => {
                        e.stopPropagation();

                        if (url && name) {
                          const lastIndexOf = fieldName.lastIndexOf('.');
                          const curFieldName = lastIndexOf === -1 ? fieldName : fieldName.slice(lastIndexOf + 1);
                          const param = {
                            menuId: curMenu.value?.id,
                            id: form?.getFieldValue('id') || '',
                            fieldName: curFieldName,
                            fileId: id || ''
                          };

                          const fileUrl = await attachmentDownload(tableName, param);
                          downloadFileByUrl(fileUrl, name);
                        }
                      }}
                    />
                  )}
                  {!detailMode && <IconDelete onClick={() => fileListMethods.deleteFile(index)} />}
                </div>
              )}
            </div>
          ))}
        </div>
      );
    };

    const rules: ITypeRules<ValidatorType.Custom>[] = [
      {
        required: verify?.required,
        type: ValidatorType.Custom,
        message: `${label.text}是必填项`
      }
    ];

    const formatAccept = (verify: any) => {
      if (!verify?.fileFormatLimit) return 'undefined';
      return verify?.fileFormat
        .split(',')
        .map((i: any) => i.trim().replace(/^\./, '').toLowerCase())
        .map((ext: any) => `.${ext}`)
        .join(',')
    };

    return (
      <Form.Item
        className="inputTextWrapperOBMobile fileUploadWrapperOBMobile"
        label={
          <>
            {label.display && <Ellipsis text={label.text} maxLine={2} />}
            {props?.tooltip && (
              <Popover content={props?.tooltip} direction='bottomCenter' >
                <IconQuestionCircle width={12} height={12} style={{ marginLeft: 6 }} />
              </Popover>
            )}
          </>
        }
        layout="vertical"
        field={fieldId}
        rules={rules}
        trigger="fileList"
        style={{
          pointerEvents: runtime ? 'unset' : 'none',
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        extra={
          <div className="fileUploadBottomTips">
            {!detailMode && uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] ? <>
              {verify?.fileFormatLimit && (
                <span>支持{verify?.fileFormat}格式{verify?.maxCountLimit || verify?.maxSizeLimit ? '，' : ''}</span>
              )}
              <span>
                {verify?.maxCountLimit && (
                  <span>
                    最多上传{verify?.maxCount && verify?.maxCount > 0 ? verify?.maxCount : 1}个文件
                    {verify?.maxSizeLimit ? '，' : ''}
                  </span>
                )}
                {verify?.maxSizeLimit && <span>单个文件不超过{verify?.maxSize || 10}MB</span>}
              </span>
            </> : undefined}
          </div>
        }
      >
        <Uploader
          accept={formatAccept(verify)}
          files={filesList}
          limit={verify?.maxCountLimit ? verify?.maxCount : 0}
          onMaxSizeExceed={() =>
            Toast.toast({
              content: '文件大小超出限制',
              duration: 2000
            })
          }
          onLimitExceed={() =>
            Toast.toast({
              content: '文件数量超出限制',
              duration: 2000
            })
          }
          renderUploadArea={() =>
            <div>
              {detailMode ? null : (
                <div className="uplaodTrigger">
                  <Button type={UploadButtonType[buttonType || 'primary']}>
                    <div className="uploadTextWrapper">
                      <IconUpload />
                      <span className="uploadText">{buttonName || '点击上传'}</span>
                    </div>
                  </Button>
                </div>
              )}
            </div>
          }
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || detailMode}
          style={{
            width: '100%'
          }}
          renderFileList={renderUploadList}
          onChange={handleChange}
          upload={handleUpload}
        />
      </Form.Item>
    );
  }
);

export default XFileUpload;
