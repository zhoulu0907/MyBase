import { Form, Input, Message, Upload, Select } from '@arco-design/web-react';
import { IconAttachment, IconFileImage } from '@arco-design/web-react/icon';
import { fetchExecTask } from '@onebase/app/src/services/app_runtime';
import { uploadFile } from '@onebase/platform-center';
import { forwardRef, useEffect, useImperativeHandle, useState, type FC } from 'react';
import { getUserPage, type PageParam } from '@onebase/platform-center';
import '../style/detailOkConfirm.less';

const Option = Select.Option;

interface ChildMethodParams {
  value: {
    buttonType: string;
    [key: string]: any;
  };
  entityData: any;
}

const enum Type {
  Transfer = '转交'
}

const FormItem = Form.Item;

const maxImgSizeMB = 20;
const maxFileSizeMB = 50;

const DetailOKConfirm: FC = forwardRef((props: any, ref: any) => {
  const [userOptions, setUserOptions] = useState<any[]>([]);
  const { onSetPopupVisible, onBack, taskId, instanceId, isRequired, defaultApprovalComment } = props;
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
      const targetHandlerId = form.getFieldValue('targetHandlerId');
      const req = {
        buttonType,
        taskId,
        instanceId,
        comment: nameValue,
        entity: entityData,
        targetHandlerId
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

  const initUserData = () => {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100
    };
    getUserPage(params)
      .then((res: any) => {
        if (Array.isArray(res?.list)) {
          const selectArr: any[] = [];
          res.list?.forEach((item: any) => {
            selectArr.push({
              userId: item.id,
              name: item.nickname
            });
          });
          setUserOptions(selectArr);
        }
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
      });
  };
  useEffect(() => {
    initUserData();
  }, []);

  return (
    <section className="detail-confirm-page">
      <Form form={form} layout="vertical">
        {props.itemData?.buttonName === Type.Transfer && (
          <FormItem
            label="转交对象"
            field="targetHandlerId"
            rules={[{ required: true, message: '请选择转交对象' }]}
            wrapperCol={{ style: { width: '100%' } }}
          >
            <Select
              placeholder="选择转交对象"
              filterOption={(inputValue, option) =>
                option.props.children?.toLowerCase().indexOf(inputValue?.toLowerCase()) >= 0
              }
              allowClear
            >
              {userOptions?.map((option: any) => (
                <Option key={option?.userId} value={option?.userId}>
                  {option.name}
                </Option>
              ))}
            </Select>
          </FormItem>
        )}
        <div className="form-item-title">审批意见</div>
        <FormItem
          field="name"
          rules={[{ required: isRequired, message: '请输入审批意见' }]}
          initialValue={defaultApprovalComment}
        >
          <Input.TextArea maxLength={500} showWordLimit placeholder="请输入审批意见" wrapperStyle={{ width: '100%' }} />
        </FormItem>
        <div className="form-item-title">
          <span>
            <IconFileImage />
            图片
          </span>
        </div>
        <FormItem field="name2">
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
        <FormItem field="name3">
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
});

export default DetailOKConfirm;
