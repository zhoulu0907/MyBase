import { Form, Select } from '@arco-design/web-react';
import { getPopupContainer } from '@onebase/ui-kit';
import React from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;

export interface DynamicCanvasCardConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | unknown[]) => void;
  configs: Record<string, unknown>;
}

const DynamicCanvasCardConfig: React.FC<DynamicCanvasCardConfigProps> = ({ handlePropsChange, configs }) => {
  // 画布卡片样式选项
  const canvasCardStyleOptions = [
    {
      key: 'CanvasCardType1',
      text: '样式1',
      value: 'CanvasCardType1'
    },
    {
      key: 'CanvasCardType2',
      text: '样式2',
      value: 'CanvasCardType2'
    }
  ];

  // 处理样式变更
  const handleStyleChange = (value: string | undefined) => {
    if (value) {
      handlePropsChange('componentName', value);
    }
  };

  return (
    <div className={styles.dynamicCanvasCardConfig}>
      {/* 样式库选择 */}
      <FormItem layout="vertical" labelAlign="left" label="样式库" className={styles.formItem}>
        <Select
          placeholder="请选择样式"
          value={(configs.componentName as string) || 'CanvasCardType1'}
          getPopupContainer={getPopupContainer}
          onChange={handleStyleChange}
        >
          {canvasCardStyleOptions.map((option) => (
            <Select.Option key={option.key} value={option.value}>
              {option.text}
            </Select.Option>
          ))}
        </Select>
      </FormItem>
    </div>
  );
};

// 注册 CanvasCard 数据配置渲染器
registerConfigRenderer('CanvasCardConfig', ({ handlePropsChange, configs }) => (
  <DynamicCanvasCardConfig handlePropsChange={handlePropsChange} configs={configs} />
));

export default DynamicCanvasCardConfig;
