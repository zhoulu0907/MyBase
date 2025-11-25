import { Form, Switch, ColorPicker } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicCollapsedStyleConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicCollapsedStyleConfig: React.FC<DynamicCollapsedStyleConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs,
  id
}) => {
  const styleConfigKey = item.key || 'style';

  const [styleConfig, setStyleConfigtKey] = useState({
    showBordered: true,
    showDivider: true,
    titleColor: 'rgb(var(--primary-7))',
    shapeColor: 'rgb(var(--primary-7))'
  });

  useEffect(() => {
    setStyleConfigtKey(configs[styleConfigKey]);
  }, [configs[styleConfigKey]]);

  const handleChange = (key: string, value: any) => {
    const newConfig = { ...configs[styleConfigKey], [key]: value };
    handlePropsChange(styleConfigKey, newConfig);
  };

  return (
    <Form.Item layout="vertical" label={item.name || '样式'} className={styles.formItem}>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>显示外边框</div>
        <Switch
          checked={styleConfig.showBordered}
          size="small"
          onChange={(value) => handleChange('showBordered', value)}
        />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>显示分割线</div>
        <Switch
          checked={styleConfig.showDivider}
          size="small"
          onChange={(value) => handleChange('showDivider', value)}
        />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>标题颜色</div>
        <ColorPicker
          value={styleConfig.titleColor}
          size="small"
          onChange={(value) => handleChange('titleColor', value)}
        />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>形状颜色</div>
        <ColorPicker
          value={styleConfig.shapeColor}
          size="small"
          onChange={(value) => handleChange('shapeColor', value)}
        />
      </div>
    </Form.Item>
  );
};
export default DynamicCollapsedStyleConfig;

registerConfigRenderer(
  CONFIG_TYPES.COLLAPSED_STYLE,
  ({ id, handlePropsChange, handleConfigsChange, item, configs }) => (
    <DynamicCollapsedStyleConfig
      id={id}
      handlePropsChange={handlePropsChange}
      handleConfigsChange={handleConfigsChange}
      item={item}
      configs={configs}
    />
  )
);
