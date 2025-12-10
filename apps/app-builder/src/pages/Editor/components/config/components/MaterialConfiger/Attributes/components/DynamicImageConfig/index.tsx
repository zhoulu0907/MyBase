import { Form, Message, Upload } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { uploadFile, getFileUrlById } from '@onebase/platform-center';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

export interface DynamicImageConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

// 允许的文件格式列表
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

const DynamicImageConfig: React.FC<DynamicImageConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const imageKey = 'imageConfig';
  const [imageConfig, setImageConfig] = useState<string>('');
  const maxSizeMB = configs.verify?.maxSize || 5;
  const maxCount = 1;

  useEffect(() => {
    if (configs[imageKey]) {
      const uploadImgUrl = getFileUrlById(configs[imageKey]);
      setImageConfig(uploadImgUrl);
    }
  }, [configs[imageKey]]);

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
    <Form.Item layout="vertical" labelAlign="left" label={'图片配置'} className={styles.formItem}>
      <div className={styles.imagesTips}>支持jpg、jpeg、png、gif格式，单张{maxSizeMB}MB以内。</div>

      <div className={styles.imagesList}>
        {!imageConfig ? (
          <div className={styles.imageBox}>
            <Upload
              limit={maxCount}
              listType="picture-card"
              showUploadList={false}
              beforeUpload={async (file) => {
                if (!allowedFormats.includes(file.type)) {
                  Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
                  return false;
                }
                // 校验大小
                const isLtMax = file.size / 1024 / 1024 < maxSizeMB;
                if (!isLtMax) {
                  Message.warning(`文件大小不能超过 ${maxSizeMB}MB`);
                  return false;
                }
              }}
              customRequest={async (option) => {
                const { onProgress, onError, onSuccess, file } = option;

                try {
                  const fileId = await handleUpload(file, onProgress);
                  const uploadImgUrl = getFileUrlById(fileId);
                  if (uploadImgUrl !== '') {
                    setImageConfig(uploadImgUrl);
                    handlePropsChange(imageKey, fileId);
                    onSuccess();
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
          </div>
        ) : (
          <div className={styles.imageBox}>
            <img src={imageConfig} />
            <IconDelete
              className={styles.icon}
              style={{
                top: 4
              }}
              onClick={() => {
                setImageConfig('');
                handlePropsChange(imageKey, '');
                Message.info(`删除成功`);
              }}
            />
          </div>
        )}
      </div>
    </Form.Item>
  );
};

export default DynamicImageConfig;

registerConfigRenderer(CONFIG_TYPES.IMAGE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicImageConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
