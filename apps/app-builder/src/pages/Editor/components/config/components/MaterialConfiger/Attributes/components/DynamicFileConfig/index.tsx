import React, { useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { Form, Upload, Message } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { uploadFile, getFileDetailById } from '@onebase/platform-center';
import styles from '../../index.module.less';

export interface DynamicFileConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicFileConfig: React.FC<DynamicFileConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const fileKey = 'fileConfig';
  const [fileConfig, setFileConfig] = useState<any[]>(configs[fileKey] || []);
  const maxSizeMB = configs.verify?.maxSize || 5;
  const maxCount = configs.verify?.maxCount || 5;

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
    <Form.Item layout="vertical" labelAlign="left" label={'文件配置'} className={styles.formItem}>
      <div>支持{maxSizeMB}MB以内。</div>

      <div className={styles.fileList}>
        {fileConfig.length < maxCount && (
          <div>
            <Upload
              limit={maxCount}
              showUploadList={false}
              beforeUpload={async (file) => {
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
                    const newFileInfo = {
                      fileId,
                      name: file.name,
                    };
                    setFileConfig((prev) => [...prev, newFileInfo]);
                    handlePropsChange(fileKey, [...fileConfig, newFileInfo]);
                    onSuccess(newFileInfo);
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
        )}

        {fileConfig.map((item, index) => (
          <div key={index} className={styles.fileItem}>
            <div className={styles.fileItemName}>{item.name}</div>
            <IconDelete
              className={styles.icon}
              style={{
                top: 4
              }}
              onClick={() => {
                const newFileConfig = fileConfig.filter((v) => v.file !== item.file)
                setFileConfig(newFileConfig);
                handlePropsChange(fileKey, newFileConfig);
                Message.info(`删除成功`);
              }}
            />
          </div>
        ))}
      </div>
    </Form.Item>
  );
};

export default DynamicFileConfig;

registerConfigRenderer(CONFIG_TYPES.FILE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicFileConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
