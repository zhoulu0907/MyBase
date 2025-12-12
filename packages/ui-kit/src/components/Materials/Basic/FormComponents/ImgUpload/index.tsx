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
  const { entityDataId } = pagesRuntimeSignal;

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
  }, [fieldValue, entityDataId.value]);

  const handleGetUrlList = async () => {
    let flag = false;
    console.log('==== fieldValue ====', fieldValue);

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

    console.log('==== newFieldValue ====', newFieldValue);

    if (flag) {
      form.setFieldValue(fieldId, newFieldValue);
    }

    // 如果没有数据或没有 entityDataId，直接返回
    if (!newFieldValue.length || !entityDataId.value || !curMenu.value?.id) {
      return;
    }

    // 处理所有文件的下载
    try {
      const urls: string[] = [];
      for (const file of newFieldValue) {
        // 检查文件是否有 fileId
        const fileId = file.response?.fileId || file.id;
        if (!fileId) {
          continue;
        }

        const param = {
          menuId: curMenu.value.id,
          id: entityDataId.value,
          fieldName,
          fileId
        };
        const url = await attachmentDownload(tableName, param);
        if (url) {
          urls.push(url);
        }
      }
      console.log('==== urls ====', urls);
      setUrlList(urls);
    } catch (error) {
      console.error('获取文件 URL 失败:', error);
      setUrlList([]);
    }
  };

  // 自定义文件列表展示
  const renderUploadList = (filesList: any[], fileProps: UploadListProps) => {
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]) {
      return (
        <div className="uplaodList-text">
          {filesList.map((file, index) => (
            <div key={file.uid} className="uplaodList-text-item">
              <Watermark
                gap={[20, 20]}
                content={imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''}
              >
                <img className="uplaodList-text-item-img" src={urlList?.[index]} alt="" />
              </Watermark>
              <div className="uplaodList-text-item-name">{file.name}</div>
              {file.percent && file.percent !== 100 ? (
                <div className="uplaodList-text-item-process">
                  <Progress color="rgb(var(--primary-7))" percent={file.percent} showText={false}></Progress>
                  <IconClose
                    className="uplaodList-text-item-process-close"
                    onClick={() => {
                      if (fileProps.onRemove) {
                        fileProps.onRemove(file);
                      }
                    }}
                  />
                </div>
              ) : (
                <div className="uplaodList-text-item-opera">
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
                        if (fileProps.onRemove) {
                          fileProps.onRemove(file);
                          setUrlList((prev) => prev.splice(index, 1));
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
    }
    if (listType == UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]) {
      return (
        <div className="uplaodList-list">
          <Grid.Row gutter={4}>
            {filesList.map((file, index: number) => (
              <Grid.Col span={12} key={file.uid}>
                <div className="uplaodList-list-item">
                  <Watermark
                    gap={[20, 20]}
                    content={imageHandle?.addWatermark && imageHandle.watermarkText ? imageHandle.watermarkText : ''}
                  >
                    <img className="uplaodList-list-item-img" src={urlList?.[index]} alt="" />
                  </Watermark>
                  <div className="uplaodList-list-item-content">
                    <div className="uplaodList-list-item-name">{file.name}</div>
                    <div className="uplaodList-list-item-size">
                      {(file?.originFile?.size || file.size) ? <span>{((file?.originFile?.size || file.size) / 1024 / 1024).toFixed(2)}MB</span> : null}
                    </div>
                  </div>
                  <IconClose
                    className="uplaodList-list-item-close"
                    onClick={() => {
                      if (fileProps.onRemove) {
                        fileProps.onRemove(file);
                        setUrlList((prev) => prev.splice(index, 1));
                      }
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
        <div className="uplaodList-card">
          {filesList.map((file, index) => (
            <Card
              key={file.uid}
              className="uplaodList-card-item"
              cover={
                <div className="uplaodList-card-item-img">
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
                title={<div className="uplaodList-card-item-name">{file.name}</div>}
                description={
                  <div className="uplaodList-card-item-footer">
                    <div className="uplaodList-card-item-size">
                      {(file?.originFile?.size || file.size) ? <span>{((file?.originFile?.size || file.size) / 1024 / 1024).toFixed(2)}MB</span> : null}
                    </div>
                    {!detailMode && (
                      <IconDelete
                        style={{ cursor: 'pointer' }}
                        onClick={() => {
                          if (fileProps.onRemove) {
                            fileProps.onRemove(file);
                            setUrlList((prev) => prev.splice(index, 1));
                          }
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
              const fileId = await handleUpload(file, onProgress);
              const uploadImgUrl = fileId ? URL.createObjectURL(file) : '';
              // 文件上传文件id
              if (uploadImgUrl !== '') {
                setUrlList((prev) => [...prev, uploadImgUrl]);
                onSuccess({ fileId: fileId });
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
          disabled={status !== STATUS_VALUES[STATUS_OPTIONS.DEFAULT] || detailMode}
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
