import DownloadLink from '@/assets/images/download_link.svg';
import { Button, Form, Message, Progress, Typography, Upload } from '@arco-design/web-react';
import { IconClose, IconDelete, IconDownload, IconUpload } from '@arco-design/web-react/icon';
import { type UploadItem, type UploadListProps } from '@arco-design/web-react/lib/Upload';
import { attachmentDownload, attachmentUpload, menuSignal } from '@onebase/app';
import { isRuntimeEnv, pagesRuntimeSignal } from '@onebase/common';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { downloadFileByUrl } from 'src/utils/downloadFile';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, UPLOAD_OPTIONS, UPLOAD_VALUES } from '../../../constants';
import './index.css';
import type { XInputFileUploadConfig } from './schema';

const XFileUpload = memo(
  (props: XInputFileUploadConfig & { runtime?: boolean; detailMode?: boolean; recordId?: string }) => {
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
      detailMode,
      recordId
    } = props;
    const [tableName, fieldName] = dataField;
    const { curMenu } = menuSignal;
    const { rowDataId } = pagesRuntimeSignal;

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

      if (runtime) {
        const res = await attachmentUpload(tableName, formData, progressAdapter);
        return res;
      } else {
        // TODO 编辑态上传预览
        return '';
      }
    };

    const { form } = Form.useFormContext();
    const fieldId =
      dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.FILE_UPLOAD}_${nanoid()}`;
    const fieldValue = Form.useWatch(fieldId, form);

    useEffect(() => {
      let flag = false;
      const newFieldValue = (fieldValue || []).map((ele: any) => {
        if (ele.id) {
          flag = true;
          return {
            uid: ele.id,
            name: ele.name,
            response: { fileId: ele.id },
            url: { fileId: ele.id },
            size: ele.size
          };
        }
        return { ...ele };
      });
      if (flag) {
        form.setFieldValue(fieldId, newFieldValue);
      }
    }, [fieldValue]);

    // 自定义文件列表展示
    const renderUploadList = (filesList: any[], props: UploadListProps) => {
      const getFileIcon = (file: UploadItem) => {
        if (file?.name) {
          // todo  根据文件类型展示不同icon
          const index = file.name.lastIndexOf('.');
          const type = file.name.slice(index + 1);
        }

        return <img src={DownloadLink} alt="download_link" />;
      };

      return (
        <div className="uplaodList-text">
          {filesList.map((file, index) => (
            <div key={file.uid} className="uplaodList-text-item">
              {getFileIcon(file)}
              <Typography.Ellipsis showTooltip className={`${showDownload ? 'uplaodList-text-item-name-hover' : ''}`}>
                {file.name}
              </Typography.Ellipsis>
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
                  {showDownload && (
                    <IconDownload
                      style={{ color: '#009E9E' }}
                      onClick={async (e) => {
                        e.stopPropagation();

                        const lastIndexOf = fieldName.lastIndexOf('.');
                        const curFieldName = lastIndexOf === -1 ? fieldName : fieldName.slice(lastIndexOf + 1);
                        const param = {
                          menuId: curMenu.value?.id,
                          id: recordId || rowDataId.value,
                          fieldName: curFieldName,
                          fileId: file.response.fileId || file.uid
                        };
                        const fileUrl = await attachmentDownload(tableName, param);
                        downloadFileByUrl(fileUrl, file.name);
                      }}
                    />
                  )}

                  {!detailMode && (
                    <IconDelete
                      onClick={() => {
                        if (props.onRemove) {
                          props.onRemove(file);
                        }
                      }}
                    />
                  )}
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
          labelCol={layout === 'horizontal' ? { span: 10 } : {}}
          rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
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
            listType="text"
            beforeUpload={async (file) => {
              const fileSizeLimit = verify?.maxSize * 1024; // 转换为kb;
              const fileSize = file.size / 1024;
              if (fileSize > fileSizeLimit) {
                Message.warning('文件大小超出限制');
                return false;
              }

              // 校验格式
              if (verify?.fileFormat) {
                const lastIndexOf = file.name.lastIndexOf('.');
                const type = file.name.slice(lastIndexOf + 1);
                if (verify.fileFormat.toLocaleLowerCase().indexOf(type.toLocaleLowerCase()) === -1) {
                  Message.warning(`不支持该格式，仅支持 ${verify.fileFormat}`);
                  return false;
                }
              }
            }}
            customRequest={async (option) => {
              const { onProgress, onError, onSuccess, file } = option;
              try {
                const fileId = await handleUpload(file, onProgress);
                const uploadFileUrl = URL.createObjectURL(file);
                // 文件上传文件id
                if (fileId && uploadFileUrl !== '') {
                  setFileUrl(uploadFileUrl);
                  onSuccess({ fileId: fileId });
                } else {
                  onError({
                    status: 'error',
                    msg: '上传失败'
                  });
                  const newFieldList = form.getFieldValue(fieldId);
                  form.setFieldValue(
                    fieldId,
                    newFieldList.filter((ele: any) => ele.status !== 'error')
                  );
                }
              } catch (error) {
                onError({
                  status: 'error',
                  msg: '上传失败'
                });
                const newFieldList = form.getFieldValue(fieldId);
                form.setFieldValue(
                  fieldId,
                  newFieldList.filter((ele: any) => ele.status !== 'error')
                );
              }
            }}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || detailMode || !isRuntimeEnv()}
            showUploadList={{
              removeIcon: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? null : <IconDelete />
            }}
            renderUploadList={renderUploadList}
          >
            {detailMode ? null : (
              <div className="uplaodTrigger">
                {uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && (
                  <Button type={buttonType || 'primary'}>
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
  }
);

export default XFileUpload;
