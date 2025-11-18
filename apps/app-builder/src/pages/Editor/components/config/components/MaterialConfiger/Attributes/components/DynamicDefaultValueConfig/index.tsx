import { Form, Select, Input, Button, Switch } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { DEFAULT_VALUE_TYPES, DEFAULT_VALUE_TYPES_LABELS, getPopupContainer } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicDefaultValueConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDefaultValueConfig: React.FC<DynamicDefaultValueConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const defaultValueConfigKey = item.key || 'defaultValueConfig';

  const [defaultValueConfig, setDefaultValueConfig] = useState({
    type: '',
    customValue: undefined
  });

  useEffect(() => {
    setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey] }));
  }, [configs[defaultValueConfigKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[defaultValueConfigKey], [key]: value };
    handlePropsChange(defaultValueConfigKey, newConfig);
  };

  return (
    <>
      <Form.Item layout="vertical" label={item.name || '默认值'} className={styles.formItem}>
        <Select
          getPopupContainer={getPopupContainer}
          onChange={(value) => handleChange('type', value)}
          value={defaultValueConfig?.type}
          options={[
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.CUSTOM], value: DEFAULT_VALUE_TYPES.CUSTOM },
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.FORMULA], value: DEFAULT_VALUE_TYPES.FORMULA }
            // { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.LINKAGE], value: DEFAULT_VALUE_TYPES.LINKAGE }
          ]}
        ></Select>
      </Form.Item>
      {/* 自定义 */}
      {defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM && (
        <Form.Item layout="vertical" className={styles.formItem}>
          {typeof defaultValueConfig?.customValue === 'boolean' ? (
            <Switch checked={defaultValueConfig?.customValue} onChange={(value) => handleChange('customValue', value)} />
          ) : (
            <Input
              value={defaultValueConfig?.customValue}
              onChange={(value) => handleChange('customValue', value)}
              placeholder="请输入"
            />
          )}
        </Form.Item>
      )}
      {/* TODO 公式计算 */}
      {defaultValueConfig?.type === DEFAULT_VALUE_TYPES.FORMULA && <Button>设置公式</Button>}
    </>
  );
};
export default DynamicDefaultValueConfig;
