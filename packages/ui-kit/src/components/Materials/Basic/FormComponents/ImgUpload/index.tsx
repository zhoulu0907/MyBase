import { Card, Form, Grid, Message, Modal, Progress, Upload, Watermark } from '@arco-design/web-react';
import { IconClose, IconDelete, IconDownload, IconEye, IconImage, IconPlus } from '@arco-design/web-react/icon';
import { type UploadListProps } from '@arco-design/web-react/lib/Upload';
import { attachmentDownload, attachmentUpload, menuSignal } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES, UPLOAD_OPTIONS, UPLOAD_VALUES } from '../../../constants';
import './index.css';
import type { XInputImgUploadConfig } from './schema';

const XImgUpload = memo((props: XInputImgUploadConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    status,
    tooltip,
    listType,
    uploadType,
    imageHandle,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;
  const [tableName, fieldName] = dataField;
  const { curMenu } = menuSignal;
  const { rowDataId } = pagesRuntimeSignal;

  const [urlList, setUrlList] = useState<string[]>([]);

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
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  useEffect(() => {
    handleGetUrlList();
  }, [fieldValue, rowDataId.value]);

  // 组件卸载时清理所有 blob URL
  useEffect(() => {
    return () => {
      urlList.forEach((url) => {
        if (url && url.startsWith('blob:')) {
          URL.revokeObjectURL(url);
        }
      });
    };
  }, []);

  const handleGetUrlList = async () => {
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

    // 如果没有数据，直接返回
    if (!newFieldValue.length) {
      return;
    }

    // 处理所有文件的 URL
    try {
      const urls: string[] = [];
      for (const file of newFieldValue) {
        // 判断是否为本地上传的文件（有 originFile 或已有本地 URL）
        const isLocalFile =
          file.originFile || (file.url && typeof file.url === 'string' && file.url.startsWith('blob:'));

        if (isLocalFile) {
          // 本地上传的文件，使用本地 URL
          if (file.originFile) {
            // 如果还没有创建本地 URL，则创建
            const localUrl = URL.createObjectURL(file.originFile);
            urls.push(localUrl);
          } else if (file.url && typeof file.url === 'string') {
            // 如果已经有本地 URL，直接使用
            urls.push(file.url);
          }
          continue;
        }

        // 已保存的文件，需要通过 attachmentDownload 接口获取 URL
        const fileId = file.response?.fileId || file.id;
        if (!fileId) {
          continue;
        }

        // 如果没有 rowDataId 或 curMenu，无法调用 attachmentDownload，跳过
        if (!rowDataId.value || !curMenu.value?.id) {
          continue;
        }

        // 兼容子表单
        const lastIndexOf = fieldName.lastIndexOf('.');
        const curFieldName = lastIndexOf === -1 ? fieldName : fieldName.slice(lastIndexOf + 1);
        const param = {
          menuId: curMenu.value.id,
          id: rowDataId.value,
          fieldName: curFieldName,
          fileId
        };

        const url = await attachmentDownload(tableName, param);
        if (url) {
          urls.push(url);
        }
      }

      // 清理旧的 blob URL，避免内存泄漏
      setUrlList((prev) => {
        prev.forEach((url) => {
          if (url && url.startsWith('blob:')) {
            URL.revokeObjectURL(url);
          }
        });
        return urls;
      });
    } catch (error) {
      console.error('获取文件 URL 失败:', error);
      // 清理旧的 blob URL
      setUrlList((prev) => {
        prev.forEach((url) => {
          if (url && url.startsWith('blob:')) {
            URL.revokeObjectURL(url);
          }
        });
        return [];
      });
    }
  };

  // 处理文件删除，清理本地 URL
  const handleRemoveFile = (file: any, index: number, fileProps: UploadListProps) => {
    if (fileProps.onRemove) {
      // 如果是本地 URL（blob URL），需要清理
      const urlToRemove = urlList[index];
      if (urlToRemove && urlToRemove.startsWith('blob:')) {
        URL.revokeObjectURL(urlToRemove);
      }
      // 从 urlList 中移除对应的 URL
      setUrlList((prev) => prev.filter((_, i) => i !== index));
      // 调用 Upload 组件的删除方法
      fileProps.onRemove(file);
    }
  };

  // 自定义文件列表展示
  const renderUploadList = (filesList: any[], fileProps: UploadListProps) => {
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]) {
      return (
        <div className="uplaodImgList-text">
          {filesList.map((file, index) => (
            <div key={file.uid} className="uplaodImgList-text-item">
              <Watermark
                gap={[20, 20]}
                content={imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''}
              >
                <img className="uplaodImgList-text-item-img" src={urlList?.[index]} alt="" />
              </Watermark>
              <div className="uplaodImgList-text-item-name">{file.name}</div>
              {file.percent && file.percent !== 100 ? (
                <div className="uplaodImgList-text-item-process">
                  <Progress color="rgb(var(--primary-7))" percent={file.percent} showText={false}></Progress>
                  <IconClose
                    className="uplaodImgList-text-item-process-close"
                    onClick={() => {
                      handleRemoveFile(file, index, fileProps);
                    }}
                  />
                </div>
              ) : (
                <div className="uplaodImgList-text-item-opera">
                  <IconEye
                    onClick={() => {
                      Modal.info({
                        title: '预览',
                        content: (
                          <Watermark
                            gap={[20, 20]}
                            content={
                              imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''
                            }
                          >
                            <img src={urlList?.[index]} width="100%" alt="" />
                          </Watermark>
                        )
                      });
                    }}
                  />
                  <IconDownload
                    onClick={() => {
                      if (file.url && file.name) {
                        // todo
                      }
                    }}
                  />
                  {!detailMode && (
                    <IconDelete
                      onClick={() => {
                        handleRemoveFile(file, index, fileProps);
                      }}
                    />
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]) {
      return (
        <div className="uplaodImgList-list">
          <Grid.Row gutter={4}>
            {filesList.map((file, index: number) => (
              <Grid.Col span={12} key={file.uid}>
                <div className="uplaodImgList-list-item">
                  <Watermark
                    gap={[20, 20]}
                    content={imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''}
                  >
                    <img className="uplaodImgList-list-item-img" src={urlList?.[index]} alt="" />
                  </Watermark>
                  <div className="uplaodImgList-list-item-content">
                    <div className="uplaodImgList-list-item-name">{file.name}</div>
                    <div className="uplaodImgList-list-item-size">
                      {file?.originFile?.size || file.size ? (
                        <span>{((file?.originFile?.size || file.size) / 1024 / 1024).toFixed(2)}MB</span>
                      ) : null}
                    </div>
                  </div>
                  <IconClose
                    className="uplaodImgList-list-item-close"
                    onClick={() => {
                      handleRemoveFile(file, index, fileProps);
                    }}
                  />
                </div>
                {file.percent && file.percent !== 100 ? (
                  <Progress color="rgb(var(--primary-7))" percent={file.percent} showText={false}></Progress>
                ) : null}
              </Grid.Col>
            ))}
          </Grid.Row>
        </div>
      );
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.CARD]) {
      return (
        <div className="uplaodImgList-card">
          {filesList.map((file, index) => (
            <Card
              key={file.uid}
              className="uplaodImgList-card-item"
              cover={
                <div className="uplaodImgList-card-item-img">
                  <Watermark
                    gap={[20, 20]}
                    content={imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''}
                  >
                    <img src={urlList?.[index]} alt="" />
                  </Watermark>
                </div>
              }
            >
              <Card.Meta
                title={<div className="uplaodImgList-card-item-name">{file.name}</div>}
                description={
                  <div className="uplaodImgList-card-item-footer">
                    <div className="uplaodImgList-card-item-size">
                      {file?.originFile?.size || file.size ? (
                        <span>{((file?.originFile?.size || file.size) / 1024 / 1024).toFixed(2)}MB</span>
                      ) : null}
                    </div>
                    {!detailMode && (
                      <IconDelete
                        style={{ cursor: 'pointer' }}
                        onClick={() => {
                          handleRemoveFile(file, index, fileProps);
                        }}
                      />
                    )}
                  </div>
                }
              />
            </Card>
          ))}
        </div>
      );
    }
    return <></>;
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
          imagePreview
          limit={
            (status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode) && fieldValue
              ? fieldValue?.length
              : verify?.maxCount === -1
                ? undefined
                : verify?.maxCount
          }
          accept={verify?.fileFormat || 'image/*'}
          listType={'text'}
          beforeUpload={async (file) => {
            // 校验大小
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
              // 本地上传的文件，直接使用本地 URL 渲染
              const uploadImgUrl = URL.createObjectURL(file);
              setUrlList((prev) => [...prev, uploadImgUrl]);

              // 上传文件获取 fileId
              const fileId = await handleUpload(file, onProgress);
              // 文件上传文件id
              if (fileId) {
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
          showUploadList={{
            removeIcon: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? null : <IconDelete />
          }}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || detailMode || !isRuntimeEnv()}
          drag={uploadType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]}
          renderUploadList={renderUploadList}
        >
          {detailMode ? null : (
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
          )}
        </Upload>
      </Form.Item>
    </div>
  );
});

export default XImgUpload;
