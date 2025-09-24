import React, { useState, useEffect } from 'react';
import { Form, Input, Upload, Message, Popover } from '@arco-design/web-react';
import { IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
// import { useAppEntityStore } from '@/store/store_entity';
// import { type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import styles from '../../index.module.less';

const FormItem = Form.Item;

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
  const [imageConfig, setImageConfig] = useState<any[]>(configs[imageKey] || []);
  const maxSizeMB = configs.verify?.maxSize || 5;
  const maxCount = 1;

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
    <FormItem layout="vertical" labelAlign="left" label={'图片配置'} className={styles.formItem}>
      <div className={styles.imagesTips}>支持jpg、jpeg、png、gif格式，单张{maxSizeMB}MB以内。</div>

      <div className={styles.imagesList}>
        {imageConfig.length < maxCount && (
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
                  const uploadImgUrl = await handleUpload(file, onProgress);
                  if (uploadImgUrl !== '') {
                    const newImageInfo = {
                      image: uploadImgUrl,
                      tetx: '',
                      url: ''
                    };
                    setImageConfig((prev) => [...prev, newImageInfo]);
                    handlePropsChange(imageKey, [...imageConfig, newImageInfo]);
                    handlePropsChange('imageUrl', newImageInfo.image);
                    onSuccess(uploadImgUrl);
                  } else {
                    handlePropsChange(imageKey, []);
                    handlePropsChange('imageUrl', '');
                    onError({
                      status: 'error',
                      msg: '上传失败'
                    });
                  }
                } catch (error) {
                  handlePropsChange(imageKey, []);
                  handlePropsChange('imageUrl', '');
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
        )}

        {imageConfig.map((item, index) => (
          <div className={styles.imageBox} key={index}>
            <img src={item.image} />
            <IconDelete
              className={styles.icon}
              style={{
                top: 4
              }}
              onClick={() => {
                setImageConfig((prev) => prev.filter((v) => v.image !== item.image));
                handlePropsChange(imageKey, []);
                handlePropsChange('imageUrl', '');
                Message.info(`删除成功`);
              }}
            />
          </div>
        ))}
      </div>
    </FormItem>
  );
};

export default DynamicImageConfig;
