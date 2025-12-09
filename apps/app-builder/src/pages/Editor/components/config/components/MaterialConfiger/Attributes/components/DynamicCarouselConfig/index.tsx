import React, { useState, useEffect } from 'react';
import { Form, Input, Upload, Message, Popover } from '@arco-design/web-react';
import { IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { uploadFile, getFileDetailById } from '@onebase/platform-center';
import styles from '../../index.module.less';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;

export interface DynamicCarouselConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

// 允许的文件格式列表
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

const DynamicCarouselConfig: React.FC<DynamicCarouselConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const carouselKey = 'carouselConfig';
  const [carouselConfig, setCarouselConfig] = useState<any[]>(configs[carouselKey] || []);

  const maxSizeMB = configs.verify?.maxSize || 5;
  const maxCount = configs.verify?.maxCount || 10;

  useEffect(() => {
    if (configs.carouselConfig.length > 0) {
      // const newData = configs.carouselConfig.map((conf, index) => ({
      //   uid: index,
      //   name: conf.image,
      //   url: conf.image
      // }));
      // setCarouselConfig(configs.carouselConfig);
    }
  }, [configs]);

  const handleTextChange = (value: string, index: number) => {
    const newData = carouselConfig.map((car, idx) => {
      if (idx === index) {
        return { ...car, text: value };
      }
      return car;
    });
    setCarouselConfig(newData);
    handlePropsChange(carouselKey, newData);
  };

  const handleUrlChange = (value: string, index: number) => {
    const newData = carouselConfig.map((car, idx) => {
      if (idx === index) {
        return { ...car, url: value };
      }
      return car;
    });
    setCarouselConfig(newData);
    handlePropsChange(carouselKey, newData);
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

  console.log({
    carouselConfig,
    configs,
    item,
    id
  });

  return (
    <FormItem layout="vertical" labelAlign="left" label={'图片配置'} className={styles.formItem}>
      <div className={styles.imagesTips}>
        支持jpg、jpeg、png、gif格式，单张{maxSizeMB}MB以内。{carouselConfig.length}/{maxCount}
      </div>

      <div className={styles.imagesList}>
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
                const uploadImgUrl = getFileDetailById(fileId);
                if (uploadImgUrl !== '') {
                  const newImageInfo = {
                    fileId,
                    text: file.name,
                    url: ''
                  };
                  setCarouselConfig((prev) => [...prev, newImageInfo]);
                  handlePropsChange(carouselKey, [...carouselConfig, newImageInfo]);
                  onSuccess(newImageInfo);
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

        {carouselConfig.map((item, index) => (
          <Popover
            trigger="click"
            position="lb"
            key={index}
            content={
              <div className={styles.imagesForm}>
                <FormItem label="图片">
                  <img className={styles.uploadedImage} src={getFileDetailById(item.fileId)} />
                </FormItem>
                <FormItem label="显示文案">
                  <Input.TextArea defaultValue={item.text} onChange={(value) => handleTextChange(value, index)} />
                </FormItem>
                <FormItem label="超链接">
                  <Input defaultValue={item.url} onChange={(value) => handleUrlChange(value, index)} />
                </FormItem>
              </div>
            }
          >
            <div className={styles.imageBox}>
              <img src={getFileDetailById(item.fileId)} />
              <IconEdit
                className={styles.icon}
                style={{
                  top: 4
                }}
              />
              <IconDelete
                className={styles.icon}
                style={{
                  top: 28
                }}
                onClick={() => {
                  setCarouselConfig((prev) => prev.filter((v) => v.image !== item.image));
                  handlePropsChange(carouselKey, carouselConfig);
                  Message.info(`删除成功`);
                }}
              />
            </div>
          </Popover>
        ))}
      </div>
    </FormItem>
  );
};

export default DynamicCarouselConfig;

registerConfigRenderer(CONFIG_TYPES.CAROUSEL, ({ id, handlePropsChange, item, configs }) => (
  <DynamicCarouselConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
