import { useState, type FC, forwardRef, useImperativeHandle } from 'react';
import { Form, Input, Upload, Message, Button } from '@arco-design/web-react';
import { IconFileImage, IconAttachment } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { fetchExecTask } from '@onebase/app/src/services/app_runtime';

import '../style/detailOkConfirm.less';

interface ChildMethodParams {
  value: {
    buttonType: string;
    [key: string]: any;
  };
  entityData: any;
}
const FormItem = Form.Item;

const maxImgSizeMB = 20;
const maxFileSizeMB = 50;

const DetailOKConfirm: FC = forwardRef((props: any, ref: any) => {
  const { onSetPopupVisible, onBack, taskId, instanceId, isRequired } = props;
  const [form] = Form.useForm();
  const [imgUpList, setImgUpList] = useState<any>();

  useImperativeHandle(ref, () => ({
    childMethod: ({ value, entityData }: ChildMethodParams) => {
      fetchExec({ value, entityData });
    }
  }));

  const fetchExec = async ({ value, entityData }: ChildMethodParams) => {
    const buttonType = value?.buttonType;
    try {
      await form.validate();
      const nameValue = form.getFieldValue('name');
      const req = {
        buttonType,
        taskId,
        instanceId,
        comment: nameValue,
        entity: entityData
      };
      await fetchExecTask(req);
      onSetPopupVisible(false);
      onBack();
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };
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

    return (
      <section className="detail-confirm-page">
        <Form form={form} layout="vertical">
          <div className="form-item-title">审批意见</div>
          <FormItem field="name" rules={[{ required: isRequired, message: '请输入审批意见' }]}>
            <Input.TextArea
              maxLength={500}
              showWordLimit
              placeholder="请输入审批意见"
              wrapperStyle={{ width: '100%' }}
            />
          </FormItem>
          <div className="form-item-title">
            <span>
              <IconFileImage />
              图片
            </span>
          </div>
          <FormItem field="name2" >
            <Upload
              limit={9}
              listType="picture-card"
              accept=".png, .jpg, .jpeg, .gif"
              showUploadList
              beforeUpload={async (file: any) => {
                // if (!['image/jpeg', 'image/png', 'image/gif'].includes(file.type)) {
                //     Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
                //     return false;
                // }
                // 校验大小
                const isLtMax = file.size / 1024 / 1024 < maxImgSizeMB;
                if (!isLtMax) {
                  Message.warning(`图片大小不能超过 ${maxImgSizeMB}MB`);
                  return false;
                }
              }}
              customRequest={async (option: any) => {
                const { onProgress, onError, onSuccess, file } = option;
                try {
                  const uploadImgUrl = await handleUpload(file, onProgress);
                  if (uploadImgUrl !== '') {
                    const newImageInfo = {
                      image: uploadImgUrl,
                      tetx: '',
                      url: ''
                    };
                    // setCarouselConfig((prev) => [...prev, newImageInfo]);
                    // handlePropsChange(carouselKey, [...carouselConfig, newImageInfo]);
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
            ></Upload>
          </FormItem>
          <div className="form-item-title">
            <span>
              <IconAttachment />
              附件
            </span>
          </div>
          <FormItem field="name3" >
            <Upload
              limit={6}
              listType="text"
              accept=".pdf, .xls, .xlsx, .pptx, .ppt, .doc, .docx"
              showUploadList
              beforeUpload={async (file: any) => {
                // 校验大小
                const isLtMax = file.size / 1024 / 1024 < maxFileSizeMB;
                if (!isLtMax) {
                  Message.warning(`文件大小不能超过 ${maxFileSizeMB}MB`);
                  return false;
                }
              }}
              customRequest={async (option: any) => {
                const { onProgress, onError, onSuccess, file } = option;
                try {
                  const uploadImgUrl = await handleUpload(file, onProgress);
                  if (uploadImgUrl !== '') {
                    const newImageInfo = {
                      image: uploadImgUrl,
                      tetx: '',
                      url: ''
                    };
                    // setCarouselConfig((prev) => [...prev, newImageInfo]);
                    // handlePropsChange(carouselKey, [...carouselConfig, newImageInfo]);
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
              style={{
                width: '100%',
                height: '100%',
                pointerEvents: 'auto'
              }}
            />
          </FormItem>
        </Form>        
      </section>
    );
})

export default DetailOKConfirm;