import { Form, Checkbox, Input } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { useEffect, useState } from 'react';

export interface DynamicImageHandleConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicImageHandleConfig: React.FC<DynamicImageHandleConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const imageHandleKey = 'imageHandle';

  const [imageHandleConfig, setImageHandleConfig] = useState({
    autoCompress: false, // 自动压缩图片
    addWatermark: false, //添加水印
    watermarkText: '' // 水印文案
  });

  useEffect(() => {
    setImageHandleConfig((prev) => ({ ...prev, ...configs[imageHandleKey] }));
  }, [configs[imageHandleKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[imageHandleKey], [key]: value };
    handlePropsChange(imageHandleKey, newConfig);
  };

  return (
    <Form.Item layout="vertical" label={'图片处理'} className={styles.formItem}>
      <div style={{marginBottom:'8px'}}>
        <Checkbox checked={imageHandleConfig.autoCompress} onChange={(value) => handleChange('autoCompress', value)}>
          自动压缩图片
        </Checkbox>
      </div>
      <div style={{marginBottom:'8px'}}>
        <Checkbox checked={imageHandleConfig.addWatermark} onChange={(value) => handleChange('addWatermark', value)}>
          添加水印
        </Checkbox>
      </div>
      {imageHandleConfig.addWatermark && (
        <div>
          <Input
            value={imageHandleConfig.watermarkText}
            placeholder="请输入"
            onChange={(value) => handleChange('watermarkText', value)}
          />
        </div>
      )}
    </Form.Item>
  );
};

export default DynamicImageHandleConfig;
